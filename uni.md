<template>
  <!-- S : 유형 3 : Popup -  에러코드 노출시 -->
  <ModalPopup
    :title="headerTitle"
    v-model="isOpen"
    :errorMessage="errorMessage"
  >
    <!-- errorMessage="응답코드(BXM401000)" -->
    <p v-html="context"></p>
    <template #footer>
      <BoxButton
        @click="closePopup"
        :text="buttonText"
      />
    </template>
  </ModalPopup>
  <!-- E : 유형 3 : Popup -  에러코드 노출시 -->
</template>
<script setup>
import { BoxButton, Icon, ModalPopup } from "@shc-nss/ui/solid";
import { defineModel } from "vue";

const isOpen = defineModel({ default: false });

const emit = defineEmits(["close"]);

const props = defineProps({
  headerTitle: { type: String, default: "" },
  errorMessage: { type: String, default: "" },
  context: { type: String, default: "" },
  buttonText: { type: String, default: "" },
});

const closePopup = () => {
  isOpen.value = false;
  emit("close");
};
</script>
