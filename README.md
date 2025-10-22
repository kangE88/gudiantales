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
      {{ voiceMessage }}
    </div>

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
import { onMounted, ref, watch } from 'vue';

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
  /** 초기 음성 메시지 */
  initialMessage?: string;
  /** 4자리 단위로 그룹핑 안내 여부 */
  enableGroupAnnouncement?: boolean;
  /** v-model:values - 입력된 숫자 배열 */
  values?: string[];
}

export interface ScKeypadEmits {
  /** 숫자 입력 이벤트 */
  (e: 'number-click', value: string): void;
  /** 삭제 버튼 클릭 이벤트 */
  (e: 'delete-click'): void;
  /** 재배열 버튼 클릭 이벤트 */
  (e: 'rearrange-click', numbers: string[]): void;
  /** 입력 상태 변경 이벤트 */
  (e: 'input-change', count: number): void;
  /** v-model:values 업데이트 이벤트 */
  (e: 'update:values', values: string[]): void;
}

// ============================================================================
// COMPONENT SETUP
// ============================================================================
const props = withDefaults(defineProps<ScKeypadProps>(), {
  showRearrange: false,
  isDarkTheme: false,
  maxLength: 16,
  initialMessage: '카드번호를 입력해주세요. 총 16자리입니다.',
  enableGroupAnnouncement: true,
  values: () => [],
});

const emit = defineEmits<ScKeypadEmits>();

// Refs
const voiceMessage = ref<string>(props.initialMessage || '카드번호를 입력해주세요. 총 16자리입니다.');
const inputCount = ref(0);
const messageBox = ref<HTMLElement | null>(null);

// 숫자 배열 (0~9)
const numbers = ref(['1', '2', '3', '4', '5', '6', '7', '8', '9', '0']);

// ============================================================================
// METHODS
// ============================================================================

// 음성 메시지 업데이트 함수
function updateVoiceMessage(text: string) {
  voiceMessage.value = text;
  if (messageBox.value) {
    messageBox.value.style.visibility = 'visible';
  }
}

// 숫자 버튼 클릭 핸들러
function handleNumberClick(number: string) {
  const currentValues = [...(props.values || [])];
  
  if (inputCount.value < props.maxLength) {
    // 00 버튼인 경우 2자리 입력으로 처리
    if (number === '00') {
      if (inputCount.value <= props.maxLength - 2) {
        inputCount.value += 2;
        currentValues.push('0', '0');
        const currentInput = inputCount.value;
        updateVoiceMessage(`총 ${props.maxLength}자리 중 ${currentInput}자리 입력 완료: 00`);
        emit('number-click', number);
        emit('update:values', currentValues);
        emit('input-change', inputCount.value);
      } else {
        updateVoiceMessage(`마지막 ${props.maxLength - inputCount.value}자리만 입력 가능합니다.`);
        return;
      }
    } else {
      // 일반 숫자 버튼
      inputCount.value++;
      currentValues.push(number);
      const currentInput = inputCount.value;
      updateVoiceMessage(`총 ${props.maxLength}자리 중 ${currentInput}번째 입력: ${number}`);
      emit('number-click', number);
      emit('update:values', currentValues);
      emit('input-change', inputCount.value);
    }
    
    // 카드번호 형식으로 그룹핑된 안내
    if (props.enableGroupAnnouncement) {
      if (inputCount.value === 4) {
        updateVoiceMessage('첫 번째 4자리 입력 완료. 다음 4자리를 입력해주세요.');
      } else if (inputCount.value === 8) {
        updateVoiceMessage('두 번째 4자리 입력 완료. 다음 4자리를 입력해주세요.');
      } else if (inputCount.value === 12) {
        updateVoiceMessage('세 번째 4자리 입력 완료. 마지막 4자리를 입력해주세요.');
      } else if (inputCount.value === props.maxLength) {
        updateVoiceMessage('카드번호 입력이 완료되었습니다. 확인 버튼을 눌러주세요.');
      }
    }
  } else {
    updateVoiceMessage('최대 입력 개수에 도달했습니다.');
  }
}

// 삭제 버튼 클릭 핸들러
function handleDeleteClick() {
  const currentValues = [...(props.values || [])];
  
  if (inputCount.value > 0) {
    // 마지막 두 자리가 00인지 확인
    const lastTwo = currentValues.slice(-2);
    const isLastInputDoubleZero = lastTwo.length === 2 && lastTwo[0] === '0' && lastTwo[1] === '0';
    
    if (isLastInputDoubleZero && inputCount.value % 2 === 0) {
      // 00 입력을 삭제하는 경우 2자리 삭제
      inputCount.value -= 2;
      currentValues.pop();
      currentValues.pop();
      updateVoiceMessage(`총 ${props.maxLength}자리 중 ${inputCount.value}자리 남음. 00이 삭제되었습니다.`);
    } else {
      // 일반 숫자 삭제
      inputCount.value--;
      currentValues.pop();
      updateVoiceMessage(`총 ${props.maxLength}자리 중 ${inputCount.value}자리 남음. 삭제되었습니다.`);
    }
    
    emit('delete-click');
    emit('update:values', currentValues);
    emit('input-change', inputCount.value);
    
    if (inputCount.value === 0) {
      updateVoiceMessage(`입력이 초기화되었습니다. ${props.initialMessage}`);
    }
  } else {
    updateVoiceMessage('삭제할 입력이 없습니다.');
  }
}

// 재배열 버튼 클릭 핸들러
function handleRearrangeClick() {
  // 0~9 숫자를 랜덤하게 재배열
  const shuffledNumbers = [...numbers.value];
  for (let i = shuffledNumbers.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    const temp = shuffledNumbers[i] as string;
    shuffledNumbers[i] = shuffledNumbers[j] as string;
    shuffledNumbers[j] = temp;
  }
  numbers.value = shuffledNumbers;
  
  // 재배열된 숫자 순서를 음성으로 안내
  const numberSequence = numbers.value.join(', ');
  updateVoiceMessage(`숫자 키패드가 재배열되었습니다. 새로운 순서는 ${numberSequence}입니다.`);
  
  emit('rearrange-click', shuffledNumbers);
}

// 입력 카운트 초기화 메서드 (외부에서 호출 가능)
function reset() {
  inputCount.value = 0;
  emit('update:values', []);
  updateVoiceMessage(props.initialMessage);
}

// isDarkTheme prop 변경 감지
watch(() => props.isDarkTheme, () => {
  // 다크 테마 변경 시 필요한 작업 수행
});

// values prop 변경 감지 - inputCount 동기화
watch(() => props.values, (newValues) => {
  if (newValues) {
    inputCount.value = newValues.length;
  }
}, { immediate: true });

// ============================================================================
// LIFECYCLE
// ============================================================================
onMounted(() => {
  updateVoiceMessage(props.initialMessage || '카드번호를 입력해주세요. 총 16자리입니다.');
});

// ============================================================================
// EXPOSE
// ============================================================================
defineExpose({
  reset,
  inputCount,
});
</script>

<style lang="scss" scoped>
@use "sass:math";
@use "sass:map";
@use "sass:meta";

// Virtual Keypad
.sc-virtual__keypad {
  background-color: var(--bg-canvas-white);
  padding: 0;
  overflow: hidden;
  
  // 키패드 배경색 변경 (bg-brand_strong-same)
  &--dark {
    background-color: var(--bg-brand-strong-same) !important;
    
    // 버튼 폰트 색상을 text-ondark_primary-same으로 변경
    .keypad-btn {
      color: var(--text-ondark-primary-same) !important;
      transition: none !important;
      
      // 기존 Button 컴포넌트와 함께 사용할 때 폰트 스타일 오버라이드
      :deep(.sv-button__label) {
        font-size: 20px;
        font-weight: 500;
        color: var(--text-ondark-primary-same) !important;
        transition: none !important;
      }
      
      // 숫자 버튼 스타일
      &--number {
        background-color: var(--bg-brand-strong-same) !important;
        color: var(--text-ondark-primary-same) !important;
        transition: none !important;
        
        :deep(.sv-button) {
          background-color: var(--bg-brand-strong-same) !important;
          color: var(--text-ondark-primary-same) !important;
          transition: none !important;
        }
        
        :deep(.sv-button__label) {
          font-size: 20px;
          font-weight: 500;
          color: var(--text-ondark-primary-same) !important;
          transition: none !important;
        }
      }
      
      // 재배열 버튼 스타일
      &--rearrange {
        background-color: var(--bg-brand-strong-same) !important;
        color: var(--text-ondark-primary-same) !important;
        font-size: var(--font-size-title-m) !important;
        font-weight: 700 !important;
        transition: none !important;
        
        :deep(.sv-button) {
          background-color: var(--bg-brand-strong-same) !important;
          color: var(--text-ondark-primary-same) !important;
          font-size: var(--font-size-title-m) !important;
          font-weight: 700 !important;
          transition: none !important;
        }
        
        :deep(.sv-button__label) {
          font-size: var(--font-size-title-m) !important;
          font-weight: 700 !important;
          line-height: var(--lineheight-title-m) !important;
          letter-spacing: var(--letterspace-title-m) !important;
          color: var(--text-ondark-primary-same) !important;
          transition: none !important;
        }
        
        &:hover {
          background-color: var(--bg-brand-strong-same) !important;
          color: var(--text-ondark-primary-same) !important;
        }
        
        &:active {
          background-color: var(--bg-brand-strong-same) !important;
          transform: scale(0.95);
          transition: none !important;
        }
      }
      
      // 삭제 버튼 스타일
      &--delete {
        background-color: var(--bg-brand-strong-same) !important;
        transition: none !important;
        
        // IconButton 컴포넌트의 배경색 오버라이드
        :deep(.sv-icon-button) {
          background-color: var(--bg-brand-strong-same) !important;
          color: var(--text-ondark-primary-same) !important;
          transition: none !important;
        }
        
        // IconButton 컴포넌트의 아이콘 스타일 오버라이드
        :deep(.sv-icon) {
          width: 36px;
          height: 36px;
          fill: var(--text-ondark-primary-same) !important;
        }
        
        // IconButton 컴포넌트 컨테이너 스타일
        :deep(.sv-icon-button__icon-container) {
          width: 28px;
          height: 28px;
          display: flex;
          align-items: center;
          justify-content: center;
        }
        
        // 기존 svg 스타일도 유지 (호환성)
        svg {
          width: 24px;
          height: 24px;
          fill: var(--text-ondark-primary-same) !important;
        }
      }
    }
  }
  
  .sc-keypad__keys {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    grid-template-rows: repeat(4, 1fr);
    gap: 4px;
    border-bottom-left-radius: 16px;
    border-bottom-right-radius: 16px;
    overflow: hidden;
    
    // 동적으로 렌더링되는 숫자 버튼들이 올바르게 배치되도록 조정
    .keypad-btn {
      &:nth-child(1) { grid-row: 1; grid-column: 1; }
      &:nth-child(2) { grid-row: 1; grid-column: 2; }
      &:nth-child(3) { grid-row: 1; grid-column: 3; }
      &:nth-child(4) { grid-row: 2; grid-column: 1; }
      &:nth-child(5) { grid-row: 2; grid-column: 2; }
      &:nth-child(6) { grid-row: 2; grid-column: 3; }
      &:nth-child(7) { grid-row: 3; grid-column: 1; }
      &:nth-child(8) { grid-row: 3; grid-column: 2; }
      &:nth-child(9) { grid-row: 3; grid-column: 3; }
      &:nth-child(10) { grid-row: 4; grid-column: 1; }
      &:nth-child(11) { grid-row: 4; grid-column: 2; }
      &:nth-child(12) { grid-row: 4; grid-column: 3; }
    }
    
    .keypad-btn {
      height: 56px;
      padding: 12px 0;
      border: none;
      border-radius: 16px;
      background-color: white;
      color: var(--text-primary);
      text-align: center;
      cursor: pointer;
      transition: none !important;
      display: flex;
      align-items: center;
      justify-content: center;
      
      // Button 컴포넌트의 기본 배경색을 투명하게 설정
      :deep(.sv-button) {
        background-color: transparent !important;
        color: inherit;
        transition: none !important;
      }
      
      &:active {
        transform: scale(0.95);
        transition: none !important;
      }
      
      // 기존 Button 컴포넌트와 함께 사용할 때 폰트 스타일 오버라이드
      :deep(.sv-button__label) {
        font-size: 20px;
        font-weight: 500;
      }
      
      &__00 {
        grid-column: span 1;
      }
      
      &__delete {
        background-color: white;
        
        svg {
          width: 24px;
          height: 24px;
          fill: var(--text-primary);
        }
      }
      
      // 숫자 버튼 스타일
      &--number {
        background-color: white;
        color: var(--text-primary);
        
        :deep(.sv-button__label) {
          font-size: 20px;
          font-weight: 500;
        }
      }
      
      // 재배열 버튼 스타일
      &--rearrange {
        background-color: var(--bg-warning-subtle);
        color: var(--text-warning);
        
        :deep(.sv-button__label) {
          font-size: 14px;
          font-weight: 500;
        }
        
        &:hover {
          background-color: var(--bg-warning-strong);
          color: var(--text-warning-strong);
        }
        
        &:active {
          background-color: var(--bg-warning-strong);
          transform: scale(0.95);
        }
      }
      
      // 삭제 버튼 스타일
      &--delete {
        background-color: white;
        
        // IconButton 컴포넌트의 아이콘 스타일 오버라이드
        :deep(.sv-icon) {
          width: 36px;
          height: 36px;
          fill: var(--text-primary);
        }
        
        // IconButton 컴포넌트 컨테이너 스타일
        :deep(.sv-icon-button__icon-container) {
          width: 28px;
          height: 28px;
          display: flex;
          align-items: center;
          justify-content: center;
        }
        
        // 기존 svg 스타일도 유지 (호환성)
        svg {
          width: 24px;
          height: 24px;
          fill: var(--text-primary);
        }
      }
    }
  }
}

// 접근성 메시지 박스
.sc-keypad__message {
  overflow: hidden;
  visibility: hidden;
  position: fixed;
  left: 0;
  bottom: 20px;
  width: 100%;
  height: 1px;
  margin: -1px;
  padding: 0;
  border: 0;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  
  // 스크린 리더에서만 읽히도록 설정
  &:not(:empty) {
    overflow: visible;
    visibility: visible;
    position: fixed;
    left: 0;
    bottom: 20px;
    width: 100%;
    height: auto;
    margin: 0;
    padding: 8px;
    clip: auto;
    font-size: 14px;
    font-weight: 400;
    white-space: normal;
    background-color: var(--bg-info-subtle);
    color: var(--text-primary);
    border-radius: 4px;
    text-align: center;
  }
}
</style>

