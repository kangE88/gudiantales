export {
  default as DropdownInputField,
  type DropdownInputBottomSheetProps,
  type DropdownInputFieldEmits,
  type DropdownInputFieldProps,
  type DropdownInputFieldSlots,
  type DropdownOption,
  type InputFieldType,
} from "./DropdownInputField.vue";

export {
  default as UniversalInputField,
  type UniversalInputFieldEmits,
  type UniversalInputFieldProps,
  type UniversalInputFieldSlots,
} from "./UniversalInputField.vue";

// Deprecated: MobileCarrierInput은 DropdownInputField로 대체되었습니다
export {
  default as MobileCarrierInput,
  type DropdownOption as CarrierOption,
  type DropdownInputBottomSheetProps as MobileCarrierBottomSheetProps,
  type DropdownInputFieldEmits as MobileCarrierInputEmits,
  type DropdownInputFieldProps as MobileCarrierInputProps,
  type DropdownInputFieldSlots as MobileCarrierInputSlots,
} from "./DropdownInputField.vue";
