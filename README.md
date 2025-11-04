<template>
    <div :class="['sc-virtual__keypad', { 'sc-virtual__keypad--dark': isDarkTheme }]">
      <!-- 접근성 메시지 박스 -->
      <div
        ref="messageBox"
        class="sc-keypad__message"
        role="status"
        aria-live="polite"
        aria-atomic="true"
      >
        {{ message }}
      </div>
  
      <div class="sc-keypad__keys">
        <!-- 동적으로 렌더링되는 숫자 버튼들 (1~9) -->
        <Button
          v-for="number in numbers.slice(0, 9)"
          :key="number"
          :label="number"
          :aria-label="`숫자 ${number} 입력`"
          class="keypad-btn keypad-btn--number"
          @mousedown="props.preventBlur ? $event.preventDefault() : null"
          @click="handleNumberClick(number)"
        />
  
        <!-- 00 또는 재배열 버튼 -->
        <Button
          v-if="!showRearrange"
          label="00"
          aria-label="숫자 00 입력"
          class="keypad-btn keypad-btn--number"
          @mousedown="props.preventBlur ? $event.preventDefault() : null"
          @click="handleNumberClick('00')"
        />
        <Button
          v-else
          label="재배열"
          aria-label="숫자 재배열"
          class="keypad-btn keypad-btn--rearrange"
          @mousedown="props.preventBlur ? $event.preventDefault() : null"
          @click="handleRearrangeClick"
        />
  
        <!-- 0 버튼 -->
        <Button
          label="0"
          :aria-label="`숫자 0 입력`"
          class="keypad-btn keypad-btn--number"
          @mousedown="props.preventBlur ? $event.preventDefault() : null"
          @click="handleNumberClick('0')"
        />
  
        <!-- 삭제 버튼 -->
        <IconButton
          iconName="delete"
          size="large"
          aria-label="삭제"
          class="keypad-btn keypad-btn--delete"
          @mousedown="props.preventBlur ? $event.preventDefault() : null"
          @click="handleDeleteClick"
        />
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { Button, IconButton } from "@shinhan/solid-vue-ui";
import { ref, watch } from "vue";
  
  // ============================================================================
  // TYPES & INTERFACES
  // ============================================================================
  export interface ScKeypadProps {
    /** 재배열 버튼 표시 여부 */
    showRearrange?: boolean;
    /** 다크 테마 사용 여부 */
    isDarkTheme?: boolean;
    /** 최대 입력 자릿수 */
    maxLength?: number;
    /** 초기 메시지  */
    initialMessage?: string;
    /** 4자리 단위로 그룹핑 안내 여부 */ //TODO:체크필요
    cardNumberGroupCheck?: boolean;
    /** v-model:values - 입력된 숫자 배열 */
    values?: string[];
    /** v-model:value - InputField와 연결용 문자열 (쉼표 없음) */
    value?: string;
    /** InputField blur 방지 (기본: true) */
    preventBlur?: boolean;
    /** 연결된 InputField의 ref (포커스 관리용) */
    inputRef?: HTMLElement | null;
  }
  
  export interface ScKeypadEmits {
    /** 숫자 입력 이벤트 */
    (e: "number-click", value: string): void;
    /** 삭제 버튼 클릭 이벤트 */
    (e: "delete-click"): void;
    /** 재배열 버튼 클릭 이벤트 */
    (e: "rearrange-click", numbers: string[]): void;
    /** 입력 상태 변경 이벤트 */
    (e: "input-change", count: number): void;
    /** v-model:values 업데이트 이벤트 (배열) */
    (e: "update:values", values: string[]): void;
    /** v-model:value 업데이트 이벤트 (문자열, InputField용) */
    (e: "update:value", value: string): void;
  }
  
  // ============================================================================
  // COMPONENT SETUP
  // ============================================================================
  const props = withDefaults(defineProps<ScKeypadProps>(), {
    showRearrange: false,
    isDarkTheme: false,
    maxLength: 4,
    initialMessage: "",
    cardNumberGroupCheck: true,
    values: () => [],
    value: "",
    preventBlur: true,
    inputRef: null,
  });
  
  const emit = defineEmits<ScKeypadEmits>();
  
  // Refs
  const message = ref<string>(props.initialMessage);
  const inputCount = ref(0);
  const messageBox = ref<HTMLElement | null>(null);
  
  // 숫자 배열 (0~9)
  const numbers = ref(["1", "2", "3", "4", "5", "6", "7", "8", "9", "0"]);
  
  // ============================================================================
  // METHODS
  // ============================================================================
  
  // 메시지 업데이트 함수
  const updateMessage = (text: string) => {
    // message.value = text;
    if (messageBox.value) {
      messageBox.value.style.visibility = "visible";
    }
  };
  
  // 숫자 버튼 클릭 핸들러
  const handleNumberClick = (number: string) => {
    const currentValues = [...(props.values || [])];
    const currentValue = props.value || "";
  
    if (inputCount.value < props.maxLength) {
      // TODO:00 버튼인 경우 2자리 입력으로 처리
      // if (number === "00") {
      //   if (inputCount.value <= props.maxLength - 2) {
      //     inputCount.value += 2;
      //     currentValues.push("0", "0");
      //     const currentInput = inputCount.value;
      //     updateMessage(`총 ${props.maxLength}자리 중 ${currentInput}자리 입력 완료: 00`);
      //     emit("number-click", number);
      //     emit("update:values", currentValues);
      //     emit("update:value", currentValue + "00");
      //     emit("input-change", inputCount.value);
      //   } else {
      //     updateMessage(`마지막 ${props.maxLength - inputCount.value}자리만 입력 가능합니다.`);
      //     return;
      //   }
      // } else {
      /**
       * 일반 숫자 버튼
       */
      inputCount.value++;
      currentValues.push(number);
      const newValue = currentValue + number;
      
      console.log("currentValues>>", currentValues.join('')); // 쉼표 없이 출력: "1234"
      console.log("currentValue>>", newValue); // InputField용 문자열
      
      const currentInput = inputCount.value;
      updateMessage(`총 ${props.maxLength}자리 중 ${currentInput}번째 입력: ${number}`);
      
      emit("number-click", number);
      emit("update:values", currentValues); // 배열 버전
      emit("update:value", newValue); // 문자열 버전 (InputField용)
      emit("input-change", inputCount.value);

      // InputField에 포커스 재설정 (옵션)
      if (props.inputRef) {
        props.inputRef.focus();
      }
      // }
  
      // 카드번호 형식으로 그룹핑된 안내
      if (props.cardNumberGroupCheck) {
        if (inputCount.value === 4) {
          updateMessage("첫 번째 4자리 입력 완료. 다음 4자리를 입력해주세요.");
        } else if (inputCount.value === 8) {
          updateMessage("두 번째 4자리 입력 완료. 다음 4자리를 입력해주세요.");
        } else if (inputCount.value === 12) {
          updateMessage("세 번째 4자리 입력 완료. 마지막 4자리를 입력해주세요.");
        } else if (inputCount.value === props.maxLength) {
          updateMessage("카드번호 입력이 완료되었습니다. 확인 버튼을 눌러주세요.");
        }
      }
    } else {
      updateMessage("최대 입력 개수에 도달했습니다.");
    }
  };
  
  // 삭제 버튼 클릭
  function handleDeleteClick() {
    const currentValues = [...(props.values || [])];
    const currentValue = props.value || "";
  
    if (inputCount.value > 0) {
      // 마지막 두 자리가 00인지 확인
      // const lastTwo = currentValues.slice(-2);
      // const isLastInputDoubleZero = lastTwo.length === 2 && lastTwo[0] === "0" && lastTwo[1] === "0";
  
      // if (isLastInputDoubleZero && inputCount.value % 2 === 0) {
      //   // 00 입력을 삭제하는 경우 2자리 삭제
      //   inputCount.value -= 2;
      //   currentValues.pop();
      //   currentValues.pop();
      //   const newValue = currentValue.slice(0, -2);
      //   updateMessage(
      //     `총 ${props.maxLength}자리 중 ${inputCount.value}자리 남음. 00이 삭제되었습니다.`
      //   );
      //   emit("update:value", newValue);
      // } else {
      //   // 일반 숫자 삭제
      inputCount.value--;
      currentValues.pop();
      const newValue = currentValue.slice(0, -1); // 마지막 문자 제거
      
      console.log("delete>>", currentValues.join('')); // 쉼표 없이 출력: "123"
      console.log("deleteValue>>", newValue); // InputField용 문자열
      
      updateMessage(`총 ${props.maxLength}자리 중 ${inputCount.value}자리 남음. 삭제되었습니다.`);
      // }
  
      emit("delete-click");
      emit("update:values", currentValues); // 배열 버전
      emit("update:value", newValue); // 문자열 버전 (InputField용)
      emit("input-change", inputCount.value);

      // InputField에 포커스 재설정 (옵션)
      if (props.inputRef) {
        props.inputRef.focus();
      }
  
      if (inputCount.value === 0) {
        updateMessage(`입력이 초기화되었습니다. ${props.initialMessage}`);
      }
    } else {
      updateMessage("삭제할 입력이 없습니다.");
    }
  }
  
  // 재배열 버튼 클릭 핸들러
  const handleRearrangeClick = () => {
    // 0~9 숫자를 랜덤하게 재배열
    const shuffledNumbers = [...numbers.value];
    for (let i = shuffledNumbers.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      const temp = shuffledNumbers[i] as string;
      shuffledNumbers[i] = shuffledNumbers[j] as string;
      shuffledNumbers[j] = temp;
    }
    numbers.value = shuffledNumbers;
  
    // 재배열된 숫자 순서를 메세지 안내
    // const numberSequence = numbers.value.join(", ");
    // updateMessage(`숫자 키패드가 재배열되었습니다. 새로운 순서는 ${numberSequence}입니다.`);
  
    emit("rearrange-click", shuffledNumbers);
  };
  
  // 입력 카운트 초기화 메서드 (외부에서 호출 가능)
  const reset = () => {
    inputCount.value = 0;
    emit("update:values", []); // 배열 초기화
    emit("update:value", ""); // 문자열 초기화
    updateMessage(props.initialMessage);
  };
  
  // isDarkTheme prop 변경 감지
  watch(
    () => props.isDarkTheme,
    () => {
      // 다크 테마 변경 시 필요한 작업 수행
    }
  );
  
  // values prop 변경 감지 - inputCount 동기화 및 clear 이벤트 대응
  watch(
    () => props.values,
    (newValues, oldValues) => {
      if (newValues) {
        // InputField의 clear button 클릭으로 배열이 초기화된 경우 감지
        if (oldValues && oldValues.length > 0 && newValues.length === 0) {
          console.log("InputField cleared (values) - keypad 상태 초기화");
          inputCount.value = 0;
          updateMessage(props.initialMessage || "입력이 초기화되었습니다.");
        } else {
          inputCount.value = newValues.length;
        }
      }
    },
    { immediate: true }
  );

  // value prop 변경 감지 - inputCount 동기화 및 clear 이벤트 대응
  watch(
    () => props.value,
    (newValue, oldValue) => {
      if (newValue !== undefined) {
        // InputField의 clear button 클릭으로 값이 초기화된 경우 감지
        if (oldValue && oldValue.length > 0 && newValue === "") {
          console.log("InputField cleared - keypad 상태 초기화");
          inputCount.value = 0;
          updateMessage(props.initialMessage || "입력이 초기화되었습니다.");
        } else {
          inputCount.value = newValue.length;
        }
      }
    },
    { immediate: true }
  );
  
  // ============================================================================
  // LIFECYCLE
  // ============================================================================
  // onMounted(() => {
  // updateMessage(props.initialMessage || "카드번호를 입력해주세요. 총 16자리입니다.");
  // });
  
  // ============================================================================
  // EXPOSE
  // ============================================================================
  defineExpose({
    reset,
    inputCount,
  });
  </script>
  
  <style lang="scss" scoped>
  @use "@assets/styles/module/_keypad" as *;
  </style>
  
