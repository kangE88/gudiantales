<template>
  <!-- 테스트용 버튼 -->
  <div class="sc-keypad__test-controls">
    <Button
      :label="`${isDarkTheme ? '라이트 테마' : '다크 테마'} (현재: ${isDarkTheme ? '다크' : '라이트'})`"
      :variant="isDarkTheme ? 'outline' : 'solid'"
      size="small"
      @click="toggleTheme"
    />
    <p style="margin: 8px 0 0 0; font-size: 12px; color: var(--text-secondary)">
      isDarkTheme: {{ isDarkTheme }}
    </p>
  </div>

  <!-- 접근성 메시지 박스 -->
  <div
    ref="messageBox"
    class="sc-keypad__message"
    role="status"
    aria-live="polite"
    aria-atomic="true"
  >
    {{ voiceMessage }}
  </div>

  <div :class="['sc-virtual__keypad', { 'sc-virtual__keypad--dark': isDarkTheme }]">
    <div class="sc-keypad__keys">
      <!-- 동적으로 렌더링되는 숫자 버튼들 (1~9) -->
      <Button
        v-for="number in numbers.slice(0, 9)"
        :key="number"
        :label="number"
        :aria-label="`숫자 ${number} 입력`"
        class="keypad-btn keypad-btn--number"
        @click="handleNumberClick(number)"
      />

      <!-- 00 또는 재배열 버튼 -->
      <Button
        v-if="!showRearrange && !isDarkTheme"
        label="00"
        aria-label="숫자 00 입력"
        class="keypad-btn keypad-btn--number"
        @click="handleNumberClick('00')"
      />
      <Button
        v-else
        label="재배열"
        aria-label="숫자 재배열"
        class="keypad-btn keypad-btn--rearrange"
        @click="handleRearrangeClick"
      />

      <!-- 0 버튼 -->
      <Button
        label="0"
        :aria-label="`숫자 0 입력`"
        class="keypad-btn keypad-btn--number"
        @click="handleNumberClick('0')"
      />

      <!-- 삭제 버튼 -->
      <IconButton
        iconName="delete"
        size="large"
        aria-label="삭제"
        class="keypad-btn keypad-btn--delete"
        @click="handleDeleteClick"
      />
    </div>
  </div>
</template>
<script setup lang="ts">
import { Button, IconButton } from "@shc-nss/ui/solid";
import { onMounted, ref } from "vue";

export interface ScKeypadProps {
  showRearrange?: boolean;
  value?: any[];
}

// Props 정의
const props = withDefaults(defineProps<ScKeypadProps>(), {
  showRearrange: false,
  value: () => [],
});

const emit = defineEmits<{
  "update:values": [value: string[]];
}>();

console.log(props.showRearrange);

// 반응형 데이터
const voiceMessage = ref("카드번호를 입력해주세요. 총 16자리입니다.");
const inputCount = ref(0);
const messageBox = ref(null);
const isDarkTheme = ref(false);
const values = ref<string[]>([]);

// 테마 토글 함수
function toggleTheme() {
  isDarkTheme.value = !isDarkTheme.value;
}

// 숫자 배열 (0~9)
const numbers = ref(["1", "2", "3", "4", "5", "6", "7", "8", "9", "0"]);

// 음성 메시지 업데이트 함수
function updateVoiceMessage(text: string) {
  voiceMessage.value = text;
  if (messageBox.value) {
    (messageBox.value as HTMLElement).style.visibility = "visible";
  }
}

// 숫자 버튼 클릭 핸들러
function handleNumberClick(number: string) {
  values.value.push(number);
  emit("update:values", values.value);

  if (inputCount.value < 16) {
    // 00 버튼인 경우 2자리 입력으로 처리
    if (number === "00") {
      if (inputCount.value <= 14) {
        // 2자리 입력 가능한지 확인
        inputCount.value += 2;
        const currentInput = inputCount.value;
        updateVoiceMessage(`총 16자리 중 ${currentInput}자리 입력 완료: 00`);
      } else {
        // 15자리일 때는 00 입력 불가
        updateVoiceMessage("마지막 1자리만 입력 가능합니다.");
        return;
      }
    } else {
      // 일반 숫자 버튼
      inputCount.value++;
      const currentInput = inputCount.value;
      updateVoiceMessage(`총 16자리 중 ${currentInput}번째 입력: ${number}`);
    }

    // 카드번호 형식으로 그룹핑된 안내
    if (inputCount.value === 4) {
      updateVoiceMessage("첫 번째 4자리 입력 완료. 다음 4자리를 입력해주세요.");
    } else if (inputCount.value === 8) {
      updateVoiceMessage("두 번째 4자리 입력 완료. 다음 4자리를 입력해주세요.");
    } else if (inputCount.value === 12) {
      updateVoiceMessage("세 번째 4자리 입력 완료. 마지막 4자리를 입력해주세요.");
    } else if (inputCount.value === 16) {
      updateVoiceMessage("카드번호 입력이 완료되었습니다. 확인 버튼을 눌러주세요.");
    }
  } else {
    updateVoiceMessage("최대 입력 개수에 도달했습니다.");
  }
  console.log("values.value::>", values.value);
}

// 삭제 버튼 클릭 핸들러
function handleDeleteClick() {
  if (inputCount.value > 0) {
    // 마지막 입력이 00인지 확인 (짝수 자리이고 마지막 두 자리가 00인 경우)
    const isLastInputDoubleZero = inputCount.value % 2 === 0 && inputCount.value >= 2;

    if (isLastInputDoubleZero) {
      // 00 입력을 삭제하는 경우 2자리 삭제
      inputCount.value -= 2;
      updateVoiceMessage(`총 16자리 중 ${inputCount.value}자리 남음. 00이 삭제되었습니다.`);
    } else {
      // 일반 숫자 삭제
      inputCount.value--;
      updateVoiceMessage(`총 16자리 중 ${inputCount.value}자리 남음. 삭제되었습니다.`);
    }

    if (inputCount.value === 0) {
      updateVoiceMessage("입력이 초기화되었습니다. 카드번호를 입력해주세요.");
    }
  } else {
    updateVoiceMessage("삭제할 입력이 없습니다.");
  }
}

// 재배열 버튼 클릭 핸들러
function handleRearrangeClick() {
  // 0~9 숫자를 랜덤하게 재배열
  const shuffledNumbers: any[] = [...numbers.value];
  for (let i = shuffledNumbers.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [shuffledNumbers[i], shuffledNumbers[j]] = [shuffledNumbers[j], shuffledNumbers[i]];
  }
  numbers.value = shuffledNumbers;

  // 재배열된 숫자 순서를 음성으로 안내
  const numberSequence = numbers.value.join(", ");
  updateVoiceMessage(`숫자 키패드가 재배열되었습니다. 새로운 순서는 ${numberSequence}입니다.`);
}

// 컴포넌트 마운트 시 초기화
onMounted(() => {
  updateVoiceMessage("카드번호를 입력해주세요. 총 16자리입니다.");
});
</script>

<style lang="scss" scoped>
@use "@assets/styles/pages/module/keypad" as *; // keypad
</style>


================================================================================================================================================================

<template>
  <InputFieldCell
    description="안내메시지"
    errorMessage="에러메시지"
    :inputItems="inputItems"
    label="인증번호"
    required
    tooltip="툴팁메시지"
    v-model="values"
    v-model:values="values"
    v-model:valuesArray="values"
    @update:modelValue="values = $event"
    @update:values="values = $event"
    @update:valuesArray="values = $event"
  />
  <ScKeypad
    :showRearrange="true"
    v-model="values"
    @update:values="values = $event"
  ></ScKeypad>
</template>

<script setup lang="ts">
import { InputFieldCell } from "@shc-nss/ui/solid";
import { ref } from "vue";
import ScKeypad from "~/components/shc/keypad/ScKeypad.vue";

const values = ref([]);
const inputItems = [
  {
    id: "cell1",
    name: "cell1",
    label: "첫 번째",
    type: "square",
  },
  {
    id: "cell2",
    name: "cell2",
    label: "두 번째",
    type: "square",
  },
  {
    id: "cell3",
    name: "cell3",
    label: "세 번째",
    type: "square",
  },
  {
    id: "cell4",
    name: "cell4",
    label: "네 번째",
    type: "square",
  },
  {
    id: "cell5",
    name: "cell5",
    label: "다섯 번째",
    type: "square",
  },
  {
    id: "cell6",
    name: "cell6",
    label: "여섯 번째",
    type: "square",
  },
];
</script>

