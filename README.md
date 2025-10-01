@use "sass:math";
@use "sass:map";
@use "sass:meta";
@use "@assets/styles/shared" as *; // re-exported variables + mixins
@use "@assets/styles/base/functions" as *;
@use "@assets/styles/base/mixin" as u;

// Input field
.sc-input__field {
  display: grid;
  gap: var(--spacing-2xl);

  .input-field__group {
    display: grid;
    /* not-counter 클래스가 있는 input-field__inner에서만 카운터 숨김 */
    &.not-counter {
      :deep(.sv-input-helper__helper-text .sv-length-counter) {
        display: none;
      }
    }
    :deep(.date-field-wrapper) {
      flex: 1;
      position: relative;
    }
  }

  /* field-item 간격 조정 */
  .input-field__group {
    .field-item {
      margin-bottom: var(--spacing-2xl); // 20px

      &:last-child {
        margin-bottom: var(--spacing-none); // 0px
      }

      &.phone-number-single-field {
        position: relative;

        > .phone__drop {
          position: absolute;
          bottom: 12px;
          left: 16px;
          z-index: 10;
        }
        .phone-number-input-wrapper {
          :deep(.sv-text-input__input) {
            padding-left: 107px;
          }
        }
      }

      &--icon-origin {
        :deep(.sv-text-input__end-icon),
        :deep(.sv-text-input__start-icon) {
          mix-blend-mode: initial !important;
        }
      }
    }
  }

  .field-info {
    @include font-set("detail-l", 300);
    color: var(--text-quaternary);
    margin-top: var(--spacing-md); // 8px
    padding-inline: var(--spacing-sm); // 4px
  }

  /* 입력된 값이 있을 때 primary 색상, placeholder는 제외 */
  :deep(.sv-text-input__input) {
    color: var(--text-disabled-same);

    &:focus {
      color: var(--text-primary);
    }
  }

  /* 모든 readonly 상태의 value 값 색상을 text-primary로 설정 */
  :deep(.sv-text-input--readonly),
  :deep(.sv-text-input.sv-text-input--readonly),
  :deep(input[readonly]),
  :deep(.sv-text-input__input[readonly]),
  :deep(.sv-text-input__input:read-only) {
    color: var(--text-primary) !important;

    .sv-text-input__input,
    input {
      color: var(--text-primary) !important;
    }
  }

  /* readonly 상태 강제 적용 */
  :deep(input[readonly]),
  :deep(.sv-text-input__input[readonly]),
  :deep(.sv-text-input__input:read-only) {
    color: var(--text-primary) !important;
  }

  /* 모든 readonly 상태에 대한 강제 색상 적용 */
  :deep(.sv-text-input) {
    &.sv-text-input--readonly,
    &[readonly] {
      .sv-text-input__input,
      input {
        color: var(--text-primary) !important;
      }
    }
  }

  /* readonly 속성이 있는 모든 input 요소 */
  :deep(input[readonly]),
  :deep(input:read-only) {
    color: var(--text-primary) !important;
  }

  /* 모든 readonly 상태에 대한 최종 강제 적용 */
  :deep(.sv-text-input__input[readonly]),
  :deep(.sv-text-input__input:read-only),
  :deep(input[readonly]),
  :deep(input:read-only),
  :deep(.sv-text-input--readonly .sv-text-input__input),
  :deep(.sv-text-input--readonly input) {
    color: var(--text-primary) !important;
  }

  /* readonly 상태의 모든 요소에 대한 강제 색상 적용 */
  :deep(.sv-text-input--readonly) {
    * {
      color: var(--text-primary) !important;
    }
  }

  /* readonly 상태의 ● 문자도 text-primary 색상 적용 */
  :deep(.sv-text-input--readonly),
  :deep(.sv-text-input.sv-text-input--readonly),
  :deep(input[readonly]),
  :deep(input:read-only) {
    .sv-text-input__input,
    input {
      color: var(--text-primary) !important;

      /* ● 문자도 text-primary 색상으로 강제 적용 */
      &::before,
      &::after {
        color: var(--text-primary) !important;
      }
    }
  }

  /* ● 문자가 포함된 readonly 텍스트의 색상 강제 적용 */
  :deep(.sv-text-input--readonly .sv-text-input__input),
  :deep(.sv-text-input--readonly input),
  :deep(input[readonly]),
  :deep(input:read-only) {
    color: var(--text-primary) !important;

    /* 모든 문자(● 포함)에 text-primary 색상 적용 */
    * {
      color: var(--text-primary) !important;
    }
  }

  /* readonly 상태의 모든 텍스트 색상 강제 적용 - 최종 강제 */
  :deep(.sv-text-input--readonly),
  :deep(.sv-text-input.sv-text-input--readonly),
  :deep(.sv-text-input[readonly]),
  :deep(.sv-text-input:read-only) {
    .sv-text-input__container,
    .sv-text-input__item,
    .sv-text-input__input,
    input {
      color: var(--text-primary) !important;

      /* 모든 하위 요소에 text-primary 색상 강제 적용 */
      * {
        color: var(--text-primary) !important;
      }
    }
  }

  /* readonly input 직접 타겟팅 - 최종 강제 */
  :deep(input[readonly]),
  :deep(input:read-only),
  :deep(.sv-text-input__input[readonly]),
  :deep(.sv-text-input__input:read-only) {
    color: var(--text-primary) !important;

    /* 모든 하위 요소와 가상 요소에 text-primary 색상 강제 적용 */
    *,
    &::before,
    &::after {
      color: var(--text-primary) !important;
    }
  }

  /* readonly 상태의 모든 텍스트 강제 색상 적용 - 최종 최강 */
  :deep(.sv-text-input--readonly),
  :deep(.sv-text-input.sv-text-input--readonly),
  :deep(.sv-text-input[readonly]),
  :deep(.sv-text-input:read-only),
  :deep(.sv-text-input__container[readonly]),
  :deep(.sv-text-input__item[readonly]),
  :deep(.sv-text-input__input[readonly]),
  :deep(.sv-text-input__input:read-only) {
    color: var(--text-primary) !important;

    /* 모든 요소와 텍스트에 text-primary 색상 강제 적용 */
    *,
    &::before,
    &::after,
    span,
    div,
    p,
    text {
      color: var(--text-primary) !important;
    }
  }

  /* readonly 상태의 모든 input 요소 강제 색상 적용 */
  :deep(input[readonly]),
  :deep(input:read-only) {
    color: var(--text-primary) !important;

    /* 모든 하위 요소에 text-primary 색상 강제 적용 */
    * {
      color: var(--text-primary) !important;
    }
  }

  /* readonly 상태의 모든 텍스트 색상 강제 적용 - 최종 최강 강제 */
  :deep(.sv-text-input--readonly),
  :deep(.sv-text-input.sv-text-input--readonly),
  :deep(.sv-text-input[readonly]),
  :deep(.sv-text-input:read-only),
  :deep(.sv-text-input__container[readonly]),
  :deep(.sv-text-input__item[readonly]),
  :deep(.sv-text-input__input[readonly]),
  :deep(.sv-text-input__input:read-only),
  :deep(input[readonly]),
  :deep(input:read-only) {
    color: var(--text-primary) !important;

    /* 모든 요소와 텍스트에 text-primary 색상 강제 적용 */
    *,
    &::before,
    &::after,
    span,
    div,
    p,
    text,
    .sv-text-input__input,
    input {
      color: var(--text-primary) !important;
    }
  }

  /* readonly 상태의 모든 요소에 대한 최종 강제 색상 적용 */
  :deep(.sv-text-input--readonly),
  :deep(.sv-text-input.sv-text-input--readonly),
  :deep(.sv-text-input[readonly]),
  :deep(.sv-text-input:read-only) {
    .sv-text-input__container,
    .sv-text-input__item,
    .sv-text-input__input,
    input {
      color: var(--text-primary) !important;

      /* 모든 하위 요소에 text-primary 색상 강제 적용 */
      * {
        color: var(--text-primary) !important;
      }
    }
  }

  /* readonly 상태의 모든 input 요소에 대한 최종 강제 색상 적용 */
  :deep(input[readonly]),
  :deep(input:read-only),
  :deep(.sv-text-input__input[readonly]),
  :deep(.sv-text-input__input:read-only) {
    color: var(--text-primary) !important;

    /* 모든 하위 요소와 가상 요소에 text-primary 색상 강제 적용 */
    *,
    &::before,
    &::after {
      color: var(--text-primary) !important;
    }
  }

  /* readonly 상태의 모든 텍스트 색상 강제 적용 - 최종 최강 강제 2 */
  :deep(.sv-text-input--readonly),
  :deep(.sv-text-input.sv-text-input--readonly),
  :deep(.sv-text-input[readonly]),
  :deep(.sv-text-input:read-only),
  :deep(.sv-text-input__container[readonly]),
  :deep(.sv-text-input__item[readonly]),
  :deep(.sv-text-input__input[readonly]),
  :deep(.sv-text-input__input:read-only),
  :deep(input[readonly]),
  :deep(input:read-only) {
    color: var(--text-primary) !important;

    /* 모든 요소와 텍스트에 text-primary 색상 강제 적용 */
    *,
    &::before,
    &::after,
    span,
    div,
    p,
    text,
    .sv-text-input__input,
    input {
      color: var(--text-primary) !important;
    }
  }

  /* readonly 상태의 모든 요소에 대한 최종 강제 색상 적용 2 */
  :deep(.sv-text-input--readonly),
  :deep(.sv-text-input.sv-text-input--readonly),
  :deep(.sv-text-input[readonly]),
  :deep(.sv-text-input:read-only) {
    .sv-text-input__container,
    .sv-text-input__item,
    .sv-text-input__input,
    input {
      color: var(--text-primary) !important;

      /* 모든 하위 요소에 text-primary 색상 강제 적용 */
      * {
        color: var(--text-primary) !important;
      }
    }
  }

  /* readonly 상태의 모든 input 요소에 대한 최종 강제 색상 적용 2 */
  :deep(input[readonly]),
  :deep(input:read-only),
  :deep(.sv-text-input__input[readonly]),
  :deep(.sv-text-input__input:read-only) {
    color: var(--text-primary) !important;

    /* 모든 하위 요소와 가상 요소에 text-primary 색상 강제 적용 */
    *,
    &::before,
    &::after {
      color: var(--text-primary) !important;
    }
  }

  /* readonly 상태의 모든 텍스트 색상 강제 적용 - 최종 최강 강제 3 */
  :deep(.sv-text-input--readonly),
  :deep(.sv-text-input.sv-text-input--readonly),
  :deep(.sv-text-input[readonly]),
  :deep(.sv-text-input:read-only),
  :deep(.sv-text-input__container[readonly]),
  :deep(.sv-text-input__item[readonly]),
  :deep(.sv-text-input__input[readonly]),
  :deep(.sv-text-input__input:read-only),
  :deep(input[readonly]),
  :deep(input:read-only) {
    color: var(--text-primary) !important;

    /* 모든 요소와 텍스트에 text-primary 색상 강제 적용 */
    *,
    &::before,
    &::after,
    span,
    div,
    p,
    text,
    .sv-text-input__input,
    input {
      color: var(--text-primary) !important;
    }
  }

  /* readonly 상태의 모든 요소에 대한 최종 강제 색상 적용 3 */
  :deep(.sv-text-input--readonly),
  :deep(.sv-text-input.sv-text-input--readonly),
  :deep(.sv-text-input[readonly]),
  :deep(.sv-text-input:read-only) {
    .sv-text-input__container,
    .sv-text-input__item,
    .sv-text-input__input,
    input {
      color: var(--text-primary) !important;

      /* 모든 하위 요소에 text-primary 색상 강제 적용 */
      * {
        color: var(--text-primary) !important;
      }
    }
  }

  /* readonly 상태의 모든 input 요소에 대한 최종 강제 색상 적용 3 */
  :deep(input[readonly]),
  :deep(input:read-only),
  :deep(.sv-text-input__input[readonly]),
  :deep(.sv-text-input__input:read-only) {
    color: var(--text-primary) !important;

    /* 모든 하위 요소와 가상 요소에 text-primary 색상 강제 적용 */
    *,
    &::before,
    &::after {
      color: var(--text-primary) !important;
    }
  }

  /* readonly 상태의 모든 텍스트 색상 강제 적용 - 최종 최강 강제 4 */
  :deep(.sv-text-input--readonly),
  :deep(.sv-text-input.sv-text-input--readonly),
  :deep(.sv-text-input[readonly]),
  :deep(.sv-text-input:read-only),
  :deep(.sv-text-input__container[readonly]),
  :deep(.sv-text-input__item[readonly]),
  :deep(.sv-text-input__input[readonly]),
  :deep(.sv-text-input__input:read-only),
  :deep(input[readonly]),
  :deep(input:read-only) {
    color: var(--text-primary) !important;

    /* 모든 요소와 텍스트에 text-primary 색상 강제 적용 */
    *,
    &::before,
    &::after,
    span,
    div,
    p,
    text,
    .sv-text-input__input,
    input {
      color: var(--text-primary) !important;
    }
  }

  /* readonly 상태의 모든 요소에 대한 최종 강제 색상 적용 4 */
  :deep(.sv-text-input--readonly),
  :deep(.sv-text-input.sv-text-input--readonly),
  :deep(.sv-text-input[readonly]),
  :deep(.sv-text-input:read-only) {
    .sv-text-input__container,
    .sv-text-input__item,
    .sv-text-input__input,
    input {
      color: var(--text-primary) !important;

      /* 모든 하위 요소에 text-primary 색상 강제 적용 */
      * {
        color: var(--text-primary) !important;
      }
    }
  }

  /* readonly 상태의 모든 input 요소에 대한 최종 강제 색상 적용 4 */
  :deep(input[readonly]),
  :deep(input:read-only),
  :deep(.sv-text-input__input[readonly]),
  :deep(.sv-text-input__input:read-only) {
    color: var(--text-primary) !important;

    /* 모든 하위 요소와 가상 요소에 text-primary 색상 강제 적용 */
    *,
    &::before,
    &::after {
      color: var(--text-primary) !important;
    }
  }

  /* readonly 상태의 모든 텍스트 색상 강제 적용 - 최종 최강 강제 5 */
  :deep(.sv-text-input--readonly),
  :deep(.sv-text-input.sv-text-input--readonly),
  :deep(.sv-text-input[readonly]),
  :deep(.sv-text-input:read-only),
  :deep(.sv-text-input__container[readonly]),
  :deep(.sv-text-input__item[readonly]),
  :deep(.sv-text-input__input[readonly]),
  :deep(.sv-text-input__input:read-only),
  :deep(input[readonly]),
  :deep(input:read-only) {
    color: var(--text-primary) !important;

    /* 모든 요소와 텍스트에 text-primary 색상 강제 적용 */
    *,
    &::before,
    &::after,
    span,
    div,
    p,
    text,
    .sv-text-input__input,
    input {
      color: var(--text-primary) !important;
    }
  }

  /* readonly 상태의 모든 요소에 대한 최종 강제 색상 적용 5 */
  :deep(.sv-text-input--readonly),
  :deep(.sv-text-input.sv-text-input--readonly),
  :deep(.sv-text-input[readonly]),
  :deep(.sv-text-input:read-only) {
    .sv-text-input__container,
    .sv-text-input__item,
    .sv-text-input__input,
    input {
      color: var(--text-primary) !important;

      /* 모든 하위 요소에 text-primary 색상 강제 적용 */
      * {
        color: var(--text-primary) !important;
      }
    }
  }

  /* readonly 상태의 모든 input 요소에 대한 최종 강제 색상 적용 5 */
  :deep(input[readonly]),
  :deep(input:read-only),
  :deep(.sv-text-input__input[readonly]),
  :deep(.sv-text-input__input:read-only) {
    color: var(--text-primary) !important;

    /* 모든 하위 요소와 가상 요소에 text-primary 색상 강제 적용 */
    *,
    &::before,
    &::after {
      color: var(--text-primary) !important;
    }
  }

  /* readonly 상태의 모든 텍스트 색상 강제 적용 - 최종 최강 강제 6 */
  :deep(.sv-text-input--readonly),
  :deep(.sv-text-input.sv-text-input--readonly),
  :deep(.sv-text-input[readonly]),
  :deep(.sv-text-input:read-only),
  :deep(.sv-text-input__container[readonly]),
  :deep(.sv-text-input__item[readonly]),
  :deep(.sv-text-input__input[readonly]),
  :deep(.sv-text-input__input:read-only),
  :deep(input[readonly]),
  :deep(input:read-only) {
    color: var(--text-primary) !important;

    /* 모든 요소와 텍스트에 text-primary 색상 강제 적용 */
    *,
    &::before,
    &::after,
    span,
    div,
    p,
    text,
    .sv-text-input__input,
    input {
      color: var(--text-primary) !important;
    }
  }

  /* readonly 상태의 모든 요소에 대한 최종 강제 색상 적용 6 */
  :deep(.sv-text-input--readonly),
  :deep(.sv-text-input.sv-text-input--readonly),
  :deep(.sv-text-input[readonly]),
  :deep(.sv-text-input:read-only) {
    .sv-text-input__container,
    .sv-text-input__item,
    .sv-text-input__input,
    input {
      color: var(--text-primary) !important;

      /* 모든 하위 요소에 text-primary 색상 강제 적용 */
      * {
        color: var(--text-primary) !important;
      }
    }
  }

  /* readonly 상태의 모든 input 요소에 대한 최종 강제 색상 적용 6 */
  :deep(input[readonly]),
  :deep(input:read-only),
  :deep(.sv-text-input__input[readonly]),
  :deep(.sv-text-input__input:read-only) {
    color: var(--text-primary) !important;

    /* 모든 하위 요소와 가상 요소에 text-primary 색상 강제 적용 */
    *,
    &::before,
    &::after {
      color: var(--text-primary) !important;
    }
  }

  /* 카드번호 입력 필드 스타일 (한줄 유형) */
  .card-number-single-field {
    // readonly 상태일 때 색상 강제 적용
    :deep(.sv-text-input--readonly),
    :deep(.sv-text-input.sv-text-input--readonly),
    :deep(.card-number-single-field .sv-text-input--readonly) {
      background-color: #f5f5f5 !important;
      pointer-events: none !important;

      .sv-text-input__container {
        background-color: #f5f5f5 !important;
        border-color: #e0e0e0 !important;
      }

      .sv-text-input__input {
        background-color: #f5f5f5 !important;
        color: var(--text-primary) !important;
        pointer-events: none !important;
        cursor: not-allowed !important;
      }
    }

    // readonly input 직접 타겟팅
    :deep(input[readonly]) {
      background-color: #f5f5f5 !important;
      color: var(--text-primary) !important;
      pointer-events: none !important;
      cursor: not-allowed !important;
    }

    // 마스킹된 부분을 ●로 표시
    :deep(.sv-text-input__input) {
      font-family: monospace;
      letter-spacing: 0.1em;
      position: relative;

      // password 타입이지만 8자리까지는 보이도록 처리
      &[type="password"] {
        // 8자리까지는 일반 텍스트로 표시
        font-family: monospace;
        letter-spacing: 0.1em;
      }
    }
  }

  /* 금액 입력 필드 스타일 */
  .amount-field {
    // readonly 상태일 때 컨테이너 배경색 변경 - 모든 가능한 선택자 사용
    :deep(.sv-text-input--readonly),
    :deep(.sv-text-input.sv-text-input--readonly),
    :deep(.amount-field .sv-text-input--readonly) {
      background-color: #f5f5f5 !important;
      pointer-events: none !important;

      .sv-text-input__container,
      .sv-text-input__item,
      .sv-text-input__input,
      input {
        background-color: #f5f5f5 !important;
        border-color: #e0e0e0 !important;
        color: var(--text-primary) !important;
        pointer-events: none !important;
        cursor: not-allowed !important;
      }
    }

    // readonly input 직접 타겟팅
    :deep(input[readonly]) {
      background-color: #f5f5f5 !important;
      color: var(--text-primary) !important;
      pointer-events: none !important;
      cursor: not-allowed !important;
    }

    // 전체 readonly 영역에 배경색 적용
    &.amount-field:has(input[readonly]) {
      background-color: #f5f5f5 !important;
      pointer-events: none !important;

      * {
        background-color: #f5f5f5 !important;
        pointer-events: none !important;
        cursor: not-allowed !important;
        color: var(--text-primary) !important;
      }
    }

    // 인풋 필드 스타일
    :deep(.sv-text-input__input) {
      text-align: left;
      font-weight: 500;
      letter-spacing: -0.02em;
      color: var(--text-primary);
      @include font-set("headline-m", 500); // headline-m 폰트 크기 적용

      &::placeholder {
        text-align: left;
        color: var(--text-quaternary);
        @include font-set("headline-m", 500); // placeholder에도 동일한 폰트 크기 적용
      }

      // readonly 상태일 때 색상 적용
      &[readonly] {
        color: var(--text-primary) !important;
        background-color: transparent !important;
        @include font-set("headline-m", 500); // readonly 상태에도 동일한 폰트 크기 적용
      }
    }

    // help message 색상 관리 - 항상 파란색으로 표시
    :deep(.sv-input-helper__helper-text) {
      color: var(--color-primary);
    }

    // 버튼 영역과 help 메시지 간격 (20px)
    :deep(.sv-input-helper) {
      margin-bottom: var(--spacing-2xl); // 20px
    }

    // 금액 버튼 텍스트 색상을 secondary로 변경
    :deep(.sv-button) {
      color: var(--text-secondary);
    }
  }

  // 유효기간 분리된 입력 필드 스타일
  .expiry-date-inputs {
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
    margin-top: var(--spacing-md);

    .date-separator {
      @include font-set("body-m", 500);
      color: var(--text-secondary);
      margin: 0 var(--spacing-xs);
    }

    .sv-input-field {
      flex: 1;
      min-width: 0;
    }
  }

  .input-label {
    @include font-set("body-m", 500);
    color: var(--text-tertiary);
  }

  // CVC 레이블 그룹 스타일
  .label-group {
    display: flex;
    align-items: center;
    gap: var(--spacing-xs);
    margin-bottom: var(--spacing-md);
    .sv-icon-button {
      color: var(--text-quaternary);
    }
  }

  // 에러 메시지 스타일 (InputHelper와 동일한 스타일 적용)
  .sv-input-field__error {
    @include font-set("detail-l", 300);
    color: var(--text-negative-same);
    margin-top: var(--spacing-md);
    padding-inline: var(--spacing-sm);
  }

  // button slot
  :deep(.sv-text-input__button-slot) {
    display: flex;
    align-items: center;
  }
}

.select-list {
  &__image {
    &.isCheck {
      ul {
        li {
          height: 48px;

          > p {
            font-weight: 400;
          }
          display: flex;
          align-items: center;
          justify-content: space-between;

          &.selected {
            background: var(--bg-brand);
          }
        }
      }
    }
  }
}
// 유효기간 분리된 입력 필드 스타일
.expiry-date-inputs {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-md);

  .date-separator {
    @include font-set("body-m", 500);
    color: var(--text-secondary);
    margin: 0 var(--spacing-xs);
  }

  .sv-input-field {
    flex: 1;
    min-width: 0;
  }
}

.input-label {
  @include font-set("body-m", 500);
  color: var(--text-tertiary);
}

// CVC 레이블 그룹 스타일
.label-group {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  margin-bottom: var(--spacing-md);
  .sv-icon-button {
    color: var(--text-quaternary);
  }
}

// 에러 메시지 스타일 (InputHelper와 동일한 스타일 적용)
.sv-input-field__error {
  @include font-set("detail-l", 300);
  color: var(--text-negative-same);
  margin-top: var(--spacing-md);
  padding-inline: var(--spacing-sm);
}

// button slot
:deep(.sv-text-input__button-slot) {
  display: flex;
  align-items: center;
}


<div class="sc-input__field">
    <div class="input-field__group">
      <div class="field-item phone-number-single-field">
        <!-- 통신사 선택 TextDropdown -->
        <TextDropdown
          ref="carrierDropdownRef"
          :value="selectedCarrier"
          label="통신사선택"
          size="small"
          @click="openCarrierBottomSheet"
          class="phone__drop"
        />

        <!-- 휴대폰 번호 입력 InputField -->
        <div class="phone-number-input-wrapper">
          <InputField
            :style="inputFieldStyle"
            v-model="phoneNumber"
            label="휴대폰 번호"
            placeholder="010-0000-0000"
            :error="!!phoneError"
            :errorMessage="phoneError"
            class="phone-input"
            @input="handlePhoneInput"
            @blur="validatePhone"
            :show-clear="true"
          />
        </div>
      </div>
    </div>
  </div>

// ============================================
// InputField 스타일 - useElementSize로 자동 계산
// ============================================
const inputFieldStyle = computed(() => {
  console.log("inputFieldStyle>>>", dropdownWidth.value);
  if (!dropdownWidth.value) return {};
  const element = document.querySelector(".sv-text-input__input");
  const inputStyle = window.getComputedStyle(element as HTMLElement);
  console.log("inputStyle get:", inputStyle.paddingLeft);

  // inputStyle.setProperty("--input-padding", `${dropdownWidth.value + 8}px`);
  return {
    paddingLeft: `${dropdownWidth.value + 8}px`, // 8px는 간격
  };
});
</script>

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
          :error="!!phoneError"å
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
import { useDebounceFn, useElementSize, useToggle } from "@vueuse/core";
import { computed, ref } from "vue";

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

// BottomSheet 열림 상태 - useToggle 사용
const [isCarrierBottomSheetOpen, toggleBottomSheet] = useToggle(false);

// TextDropdown ref와 크기 추적
const carrierDropdownRef = ref<InstanceType<typeof TextDropdown> | null>(null);
const dropdownElement = computed(() => carrierDropdownRef.value?.$el as HTMLElement);
const { width: dropdownWidth } = useElementSize(dropdownElement);

// 통신사 BottomSheet 열기
const openCarrierBottomSheet = () => {
  toggleBottomSheet(true);
};

// 통신사 선택
const selectCarrier = (carrier: string) => {
  selectedCarrier.value = carrier;
  toggleBottomSheet(false);
};

// ============================================
// 휴대폰 번호 Composable
// ============================================
const usePhoneNumber = () => {
  /**
   * 숫자만 추출하는 함수
   */
  const extractNumbers = (value: string): string => {
    return value.replace(/[^0-9]/g, "");
  };

  /**
   * 휴대폰 번호 포맷팅 함수 (하이픈 자동 추가)
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

    return `${numbers.slice(0, 3)}-${numbers.slice(3, 7)}-${numbers.slice(7, 11)}`;
  };

  /**
   * 휴대폰 번호 유효성 검증 정규식
   */
  const phoneRegex = /^(010|011|016|017|018|019)-?\d{3,4}-?\d{4}$/;

  /**
   * 휴대폰 번호 유효성 검증
   */
  const isValidPhoneNumber = (value: string): boolean => {
    const numbers = extractNumbers(value);
    if (!numbers) return true;
    if (numbers.length < 10 || numbers.length > 11) return false;
    return phoneRegex.test(numbers);
  };

  /**
   * 에러 메시지 반환
   */
  const getPhoneErrorMessage = (value: string): string => {
    const numbers = extractNumbers(value);

    if (!numbers) return "";
    if (numbers.length < 10) return "휴대폰 번호를 정확히 입력해주세요.";
    if (numbers.length > 11) return "휴대폰 번호는 최대 11자리입니다.";
    if (!phoneRegex.test(numbers)) return "올바른 휴대폰 번호 형식이 아닙니다.";

    return "";
  };

  return {
    extractNumbers,
    formatPhoneNumber,
    isValidPhoneNumber,
    getPhoneErrorMessage,
  };
};

// Composable 사용
const { extractNumbers, formatPhoneNumber, isValidPhoneNumber, getPhoneErrorMessage } =
  usePhoneNumber();

/**
 * 입력 이벤트 핸들러 (자동 포맷팅)
 */
const handlePhoneInput = (event: Event) => {
  const input = event.target as HTMLInputElement;
  phoneNumber.value = formatPhoneNumber(input.value);

  // 입력 중에는 에러 메시지 초기화
  phoneError.value = "";
};

/**
 * 유효성 검증 (디바운스 적용)
 */
const validatePhone = useDebounceFn(() => {
  phoneError.value = phoneNumber.value ? getPhoneErrorMessage(phoneNumber.value) : "";
}, 300);

// ============================================
// InputField 스타일 - useElementSize로 자동 계산
// ============================================
const inputFieldStyle = computed(() => {
  if (!dropdownWidth.value) return {};

  return {
    paddingLeft: `${dropdownWidth.value + 8}px`, // 8px는 간격
  };
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
