<template>
  <div :class="['universal-input-field', wrapperClass]">
    <InputField
      ref="inputFieldRef"
      v-bind="mergedInputFieldProps"
      v-model:values="internalValues"
      @update:values="handleValuesUpdate"
      @input="emit('input', $event)"
      @focus="emit('focus', $event)"
      @blur="emit('blur', $event)"
      @clear="emit('clear', $event)"
      @item-input="emit('item-input', $event)"
      @item-focus="emit('item-focus', $event)"
      @item-blur="emit('item-blur', $event)"
      @item-clear="emit('item-clear', $event)"
    >
      <!-- Forward all slots -->
      <template
        v-if="$slots.startIconSvg"
        #startIconSvg="slotProps"
      >
        <slot
          name="startIconSvg"
          v-bind="slotProps"
        />
      </template>
      <template
        v-if="$slots.endIconSvg"
        #endIconSvg="slotProps"
      >
        <slot
          name="endIconSvg"
          v-bind="slotProps"
        />
      </template>
      <template
        v-if="$slots.button"
        #button="slotProps"
      >
        <slot
          name="button"
          v-bind="slotProps"
        />
      </template>
      <template
        v-if="$slots.endAdornment"
        #endAdornment="slotProps"
      >
        <slot
          name="endAdornment"
          v-bind="slotProps"
        />
      </template>
      <template
        v-if="$slots.startDropbox"
        #startDropbox="slotProps"
      >
        <slot
          name="startDropbox"
          v-bind="slotProps"
        />
      </template>
      <template
        v-if="$slots.endDropbox"
        #endDropbox="slotProps"
      >
        <slot
          name="endDropbox"
          v-bind="slotProps"
        />
      </template>
    </InputField>

    <!-- field-info 슬롯 (InputHelper 하단에 위치) -->
    <p
      v-if="fieldInfo"
      class="field-info"
    >
      {{ fieldInfo }}
    </p>
    <slot name="fieldInfo" />

    <!-- 추가 컨텐츠 슬롯 (금액 버튼 등) -->
    <slot name="additional" />
  </div>
</template>

<script lang="ts">
import type { TextInputEmits, TextInputProps, TextInputSlots } from "@/components/InputField/types";

export type InputFieldType =
  | "text"
  | "phone"
  | "phone-with-carrier"
  | "card-pin"
  | "card-number-single"
  | "card-number-split"
  | "rrn"
  | "business-number"
  | "union-branch"
  | "english-name"
  | "amount";

export interface UniversalInputFieldProps {
  /**
   * 입력 필드 타입 (미리 정의된 타입)
   */
  type?: InputFieldType;

  /**
   * v-model:values
   */
  modelValue?: Record<string, string>;

  /**
   * 필드 정보 텍스트 (InputHelper 하단에 표시)
   */
  fieldInfo?: string;

  /**
   * 래퍼 클래스 (amount-field, card-number-single-field 등)
   */
  wrapperClass?: string;

  /**
   * InputField의 모든 props 사용 가능
   */
  inputFieldProps?: Partial<TextInputProps>;
}

export interface UniversalInputFieldEmits extends TextInputEmits {
  "update:modelValue": [values: Record<string, string>];
}

export type UniversalInputFieldSlots = TextInputSlots & {
  fieldInfo?: () => any;
  additional?: () => any;
};
</script>

<script setup lang="ts">
import { InputField } from "@/components/InputField";
import { computed, ref, watch } from "vue";

const props = withDefaults(defineProps<UniversalInputFieldProps>(), {
  type: "text",
  modelValue: () => ({}),
  wrapperClass: "",
});

const emit = defineEmits<UniversalInputFieldEmits>();
defineSlots<UniversalInputFieldSlots>();

// Refs
const inputFieldRef = ref<InstanceType<typeof InputField> | null>(null);
const internalValues = ref(props.modelValue);

// Watch external changes
watch(
  () => props.modelValue,
  (newValue) => {
    internalValues.value = newValue;
  },
  { deep: true }
);

// ============================================
// Type별 기본 inputItems 정의
// ============================================
const inputItemsMap: Record<InputFieldType, any[]> = {
  text: [
    {
      id: "text",
      name: "text",
      type: "text",
      placeholder: "텍스트 입력",
      length: 50,
    },
  ],

  phone: [
    {
      id: "phoneNumber",
      name: "phoneNumber",
      type: "tel",
      placeholder: "휴대폰번호 입력",
      length: 11,
      inputmode: "numeric",
      mask: {
        mask: "###-####-####",
        overwrite: false,
      },
    },
  ],

  "phone-with-carrier": [
    {
      id: "phoneNumber",
      name: "phoneNumber",
      type: "tel",
      placeholder: "010-0000-0000",
      length: 11,
      inputmode: "numeric",
      mask: {
        mask: "###-####-####",
        overwrite: false,
      },
    },
  ],

  "card-pin": [
    {
      id: "cardPin",
      name: "cardPin",
      type: "tel",
      placeholder: "카드 비밀번호 입력",
      length: 4,
      inputmode: "numeric",
      mask: {
        mask: "####",
        overwrite: false,
        definitions: {
          "#": { mask: "#", displayChar: "●" },
        },
      },
    },
  ],

  "card-number-single": [
    {
      id: "cardNumber",
      name: "cardNumber",
      type: "text",
      placeholder: "(-) 없이 숫자만 입력",
      inputmode: "numeric",
    },
  ],

  "card-number-split": [
    {
      id: "cardNumber1",
      name: "cardNumber1",
      type: "tel",
      placeholder: "1234",
      inputmode: "numeric",
      mask: {
        mask: "####",
        definitions: {
          "#": { mask: "#", placeholderChar: "" },
        },
      },
    },
    {
      id: "cardNumber2",
      name: "cardNumber2",
      type: "tel",
      placeholder: "5678",
      inputmode: "numeric",
      mask: {
        mask: "####",
        definitions: {
          "#": { mask: "#", placeholderChar: "" },
        },
      },
    },
    {
      id: "cardNumber3",
      name: "cardNumber3",
      type: "tel",
      placeholder: "●●●●",
      inputmode: "numeric",
      length: 4,
      mask: {
        mask: "####",
        definitions: {
          "#": { mask: "#", displayChar: "●", placeholderChar: "" },
        },
      },
    },
    {
      id: "cardNumber4",
      name: "cardNumber4",
      type: "tel",
      placeholder: "●●●●",
      inputmode: "numeric",
      length: 4,
      mask: {
        mask: "####",
        definitions: {
          "#": { mask: "#", displayChar: "●", placeholderChar: "" },
        },
      },
    },
  ],

  rrn: [
    {
      id: "rrn1",
      name: "rrn1",
      type: "tel",
      placeholder: "생년월일 6자리",
      mask: "######",
      length: 6,
      inputmode: "numeric",
    },
    {
      id: "rrn2",
      name: "rrn2",
      type: "tel",
      placeholder: "뒷자리 첫번째 숫자 입력",
      length: 1,
      inputmode: "numeric",
      mask: {
        mask: "nMMMMMM",
        overwrite: true,
        definitions: {
          n: { mask: "#", placeholderChar: "○" },
          m: { mask: "#", displayChar: "●", placeholderChar: "○" },
          M: { mask: "#", displayChar: "●", placeholderChar: "●" },
        },
        clearIncomplete: false,
        skipInvalid: true,
        lazy: false,
        eager: true,
      },
    },
  ],

  "business-number": [
    {
      id: "businessNumber",
      name: "businessNumber",
      type: "tel",
      placeholder: "사업자등록번호 입력",
      length: 10,
      inputmode: "numeric",
    },
  ],

  "union-branch": [
    {
      id: "unionBranch",
      name: "unionBranch",
      type: "text",
      placeholder: "노동조합지부 입력",
      length: 20,
      inputmode: "text",
    },
  ],

  "english-name": [
    {
      id: "englishName",
      name: "englishName",
      type: "text",
      placeholder: "영문이름",
      length: 50,
      inputmode: "latin",
      lang: "en-US",
    },
  ],

  amount: [
    {
      id: "amount",
      name: "amount",
      type: "tel",
      placeholder: "1만원 단위로 입력",
      length: 15,
      inputmode: "numeric",
    },
  ],
};

// ============================================
// Type별 기본 설정
// ============================================
const typeDefaultProps: Record<InputFieldType, Partial<TextInputProps>> = {
  text: {
    variant: "outline",
  },
  phone: {
    label: "휴대폰번호",
    variant: "outline",
  },
  "phone-with-carrier": {
    label: "휴대폰 번호",
    variant: "outline",
  },
  "card-pin": {
    label: "카드 비밀번호",
    variant: "outline",
  },
  "card-number-single": {
    label: "카드번호",
    variant: "outline",
    align: "start",
  },
  "card-number-split": {
    label: "카드번호",
    variant: "outline",
    align: "center",
  },
  rrn: {
    label: "주민등록번호",
    variant: "outline",
  },
  "business-number": {
    label: "사업자등록번호",
    variant: "outline",
  },
  "union-branch": {
    label: "노동조합지부",
    variant: "outline",
  },
  "english-name": {
    label: "영문성명",
    variant: "outline",
  },
  amount: {
    variant: "underline",
  },
};

// ============================================
// Merged Props
// ============================================
const mergedInputFieldProps = computed<Partial<TextInputProps>>(() => ({
  // Type별 기본 inputItems
  inputItems: inputItemsMap[props.type],

  // Type별 기본 props
  ...typeDefaultProps[props.type],

  // 사용자 정의 props (최우선)
  ...props.inputFieldProps,
}));

// ============================================
// Event Handlers
// ============================================
const handleValuesUpdate = (values: Record<string, string>) => {
  internalValues.value = values;
  emit("update:modelValue", values);
};

// ============================================
// Expose
// ============================================
defineExpose({
  inputFieldRef,
  values: internalValues,
});
</script>

<style lang="scss" scoped>
@use "./_input-field" as *; // Input field 모듈 참조
</style>
