<route lang="yaml">
meta:
  title: Mobile Carrier Input
  description: 통신사 선택과 휴대폰 번호 입력 컴포넌트
  author: SHC
  category: Form
</route>

<template>
  <div class="mobile-carrier-input-page">
    <h1>통신사 선택 + 휴대폰 번호 입력</h1>
    <p>통신사를 선택하고 휴대폰 번호를 입력할 수 있는 컴포넌트입니다.</p>

    <div class="demo-section">
      <h2>기본 예제</h2>

      <!-- 통신사 선택 + 휴대폰 번호 입력 필드 -->
      <div class="carrier-phone-field">
        <!-- 통신사 선택 TextDropdown -->
        <TextDropdown
          ref="carrierDropdownRef"
          :value="selectedCarrier"
          placeholder="통신사 선택"
          size="medium"
          @click="openCarrierBottomSheet"
          class="carrier-dropdown"
        />

        <!-- 휴대폰 번호 입력 InputField -->
        <InputField
          v-model="phoneNumber"
          label="휴대폰 번호"
          placeholder="010-0000-0000"
          :style="inputFieldStyle"
          :error="!!phoneError"
          :errorMessage="phoneError"
          class="phone-input"
          @input="handlePhoneInput"
          @blur="validatePhone"
        />
      </div>

      <!-- BottomSheet: 통신사 선택 -->
      <BottomSheet
        v-model="isCarrierBottomSheetOpen"
        title="통신사 선택"
        variant="closeButton"
        :dimmed="true"
        :closableDimm="true"
      >
        <div class="carrier-options">
          <button
            v-for="carrier in carriers"
            :key="carrier"
            class="carrier-option-btn"
            :class="{ 'carrier-option-btn--selected': selectedCarrier === carrier }"
            @click="selectCarrier(carrier)"
          >
            {{ carrier }}
          </button>
        </div>
      </BottomSheet>

      <!-- 선택된 값 표시 -->
      <div class="result-display">
        <h3>입력된 값</h3>
        <p><strong>통신사:</strong> {{ selectedCarrier || "선택 안 됨" }}</p>
        <p><strong>휴대폰 번호:</strong> {{ phoneNumber || "입력 안 됨" }}</p>
        <p v-if="phoneNumber"><strong>숫자만:</strong> {{ extractNumbers(phoneNumber) }}</p>
        <p v-if="phoneNumber">
          <strong>유효성:</strong>
          <span :class="isValidPhoneNumber(phoneNumber) ? 'valid' : 'invalid'">
            {{ isValidPhoneNumber(phoneNumber) ? "✓ 유효함" : "✗ 유효하지 않음" }}
          </span>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { BottomSheet } from "@/components/BottomSheet";
import { TextDropdown } from "@/components/Dropdown";
import { InputField } from "@/components/InputField";
import { computed, onMounted, ref } from "vue";

// 통신사 목록
const carriers = ref<string[]>([
  "SKT",
  "KT",
  "LG",
  "LG U+",
  "SKT 알뜰폰",
  "KT 알뜰폰",
  "LG U+ 알뜰폰",
]);

// 선택된 통신사
const selectedCarrier = ref<string>("");

// 휴대폰 번호
const phoneNumber = ref<string>("");
const phoneError = ref<string>("");

// BottomSheet 열림 상태
const isCarrierBottomSheetOpen = ref<boolean>(false);

// TextDropdown 참조
const carrierDropdownRef = ref<InstanceType<typeof TextDropdown> | null>(null);

// 통신사 BottomSheet 열기
const openCarrierBottomSheet = () => {
  isCarrierBottomSheetOpen.value = true;
};

// 통신사 선택
const selectCarrier = (carrier: string) => {
  selectedCarrier.value = carrier;
  isCarrierBottomSheetOpen.value = false;
};

// ============================================
// 휴대폰 번호 정규식 및 유틸리티 함수
// ============================================

/**
 * 숫자만 추출하는 함수
 */
const extractNumbers = (value: string): string => {
  return value.replace(/[^0-9]/g, "");
};

/**
 * 휴대폰 번호 포맷팅 함수 (하이픈 자동 추가)
 * @param value - 입력된 번호
 * @returns 포맷팅된 번호 (예: 010-1234-5678)
 */
const formatPhoneNumber = (value: string): string => {
  const numbers = extractNumbers(value);

  if (numbers.length <= 3) {
    return numbers;
  } else if (numbers.length <= 7) {
    return `${numbers.slice(0, 3)}-${numbers.slice(3)}`;
  } else if (numbers.length <= 11) {
    return `${numbers.slice(0, 3)}-${numbers.slice(3, 7)}-${numbers.slice(7, 11)}`;
  }

  // 11자리 초과시 11자리까지만 포맷팅
  return `${numbers.slice(0, 3)}-${numbers.slice(3, 7)}-${numbers.slice(7, 11)}`;
};

/**
 * 휴대폰 번호 유효성 검증 정규식
 * - 010, 011, 016, 017, 018, 019로 시작
 * - 총 10-11자리 (하이픈 제외)
 */
const phoneRegex = /^(010|011|016|017|018|019)-?\d{3,4}-?\d{4}$/;

/**
 * 휴대폰 번호 유효성 검증 함수
 * @param value - 검증할 전화번호
 * @returns 유효하면 true, 아니면 false
 */
const isValidPhoneNumber = (value: string): boolean => {
  const numbers = extractNumbers(value);

  // 빈 값은 에러 아님
  if (!numbers) return true;

  // 10자리 또는 11자리 체크
  if (numbers.length < 10 || numbers.length > 11) return false;

  // 정규식 검증
  return phoneRegex.test(numbers);
};

/**
 * 휴대폰 번호 에러 메시지 반환
 * @param value - 검증할 전화번호
 * @returns 에러 메시지 또는 빈 문자열
 */
const getPhoneErrorMessage = (value: string): string => {
  const numbers = extractNumbers(value);

  if (!numbers) return "";

  if (numbers.length < 10) {
    return "휴대폰 번호를 정확히 입력해주세요.";
  }

  if (numbers.length > 11) {
    return "휴대폰 번호는 최대 11자리입니다.";
  }

  if (!phoneRegex.test(numbers)) {
    return "올바른 휴대폰 번호 형식이 아닙니다.";
  }

  return "";
};

/**
 * 입력 이벤트 핸들러 (자동 포맷팅)
 */
const handlePhoneInput = (event: Event) => {
  const input = event.target as HTMLInputElement;
  const formatted = formatPhoneNumber(input.value);
  phoneNumber.value = formatted;

  // 입력 중에는 에러 메시지 초기화
  if (phoneError.value) {
    phoneError.value = "";
  }
};

/**
 * blur 이벤트 핸들러 (유효성 검증)
 */
const validatePhone = () => {
  if (phoneNumber.value) {
    phoneError.value = getPhoneErrorMessage(phoneNumber.value);
  }
};

// ============================================
// InputField의 padding-left를 TextDropdown의 width만큼 설정
// ============================================
const inputFieldStyle = computed(() => {
  if (!carrierDropdownRef.value) return {};

  // TextDropdown의 실제 width를 가져옴
  const dropdownEl = carrierDropdownRef.value.$el as HTMLElement;
  if (!dropdownEl) return {};

  const dropdownWidth = dropdownEl.offsetWidth;
  return {
    paddingLeft: `${dropdownWidth + 8}px`, // 8px는 간격
  };
});

onMounted(() => {
  // 컴포넌트 마운트 후 스타일 재계산 트리거
  setTimeout(() => {
    // Force re-render to calculate width
  }, 100);
});
</script>

<style lang="scss" scoped>
.mobile-carrier-input-page {
  padding: 24px;
  max-width: 800px;
  margin: 0 auto;

  h1 {
    font-size: 28px;
    font-weight: bold;
    margin-bottom: 12px;
    color: #1a1a1a;
  }

  p {
    font-size: 16px;
    color: #666;
    margin-bottom: 32px;
  }

  h2 {
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 16px;
    color: #333;
  }

  h3 {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 12px;
    color: #333;
  }
}

.demo-section {
  margin-bottom: 40px;
}

.carrier-phone-field {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 24px;
  position: relative;

  .carrier-dropdown {
    min-width: 120px;
    flex-shrink: 0;
  }

  .phone-input {
    flex: 1;
  }
}

// BottomSheet 내부 통신사 옵션 스타일
.carrier-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px;

  .carrier-option-btn {
    width: 100%;
    padding: 16px 20px;
    font-size: 16px;
    font-weight: 500;
    text-align: left;
    background-color: #f5f5f5;
    border: 2px solid transparent;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      background-color: #e8e8e8;
    }

    &:active {
      transform: scale(0.98);
    }

    &--selected {
      background-color: #0046ff;
      color: white;
      border-color: #0046ff;
      font-weight: 600;
    }
  }
}

// 결과 표시 영역
.result-display {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  border: 1px solid #e0e0e0;

  p {
    margin: 8px 0;
    font-size: 14px;
    color: #333;

    strong {
      font-weight: 600;
      color: #000;
    }

    .valid {
      color: #22c55e;
      font-weight: 600;
    }

    .invalid {
      color: #ef4444;
      font-weight: 600;
    }
  }
}
</style>
