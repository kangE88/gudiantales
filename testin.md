<template>
  <ModalPopup
    title=""
    v-model="isOpen"
    align="center"
  >
    <template #header>
      <Icon v-bind="iconProps" />
      <!-- :name="iconName"
      :size="iconSize" -->
      <h2 class="sv-popup__title">{{ headerTitle }}</h2>
    </template>
    <p v-html="context"></p>
    <template #footer>
      <BoxButton
        v-bind="buttonAttrs"
        @click="closePopup"
      />
    </template>
  </ModalPopup>
</template>
<script setup>
import { BoxButton, Icon, ModalPopup } from "@shc-nss/ui/solid";
import { computed, defineModel, defineOptions, useAttrs } from "vue";

const isOpen = defineModel({ default: false });
const emit = defineEmits(["close"]);

const attrs = useAttrs();

const props = defineProps({
  headerTitle: {
    type: String,
    default: "",
  },
  context: {
    type: String,
    default: "",
  },
});

const ICON_PROPS = ["name", "size"];
const BOX_BUTTON_PROPS = ["color", "size", "disabled", "text"];
const iconProps = computed(() => {
  const result = {};
  for (const key in attrs) {
    if (ICON_PROPS.includes(key)) {
      result[key] = attrs[key];
    }
  }
  return result;
});
const buttonAttrs = computed(() => {
  const result = {};
  for (const key in attrs) {
    if (BOX_BUTTON_PROPS.includes(key)) {
      result[key] = attrs[key];
    }
  }
  return result;
});

const closePopup = () => {
  isOpen.value = false;
  emit("close");
};

defineOptions({
  inheritAttrs: false,
});
</script>
