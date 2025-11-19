<template>
  <Infobox
    :class="classType"
    color="info"
    title="메인 텍스트"
  >
    <UnorderedList :gap="listGap">
      <UnorderedListItem
        v-for="item in infoItems"
        :level="item.level"
        :text="item.text"
        :size="item.size"
        :variant="item.variant"
      >
      </UnorderedListItem>
    </UnorderedList>
    <slot name="customItem" />
  </Infobox>
</template>

<script setup>
import { Infobox, UnorderedList, UnorderedListItem } from "@shc-nss/ui/solid";
import { computed } from "vue";

// const infoVariant = ["bullet", "dash", "star"];

const props = defineProps({
  listGap: {
    type: String,
    default: "",
  },
  infoItems: {
    type: Array,
    default: () => [],
  },
  classType: {
    type: String,
    default: "",
  },
});
const classType = computed(() => props.classType);
const listGap = computed(() => props.infoGap);
</script>

<style lang="scss" scoped>
@use "@assets/styles/module/_infobox" as *; // infobox 모듈
</style>



<div class="demo-title">유형 4 : UnorderedList 가 아닌 다른형태의 infoBox</div>
  <infoBoxCustom
    classType="infobox__header--none"
    :infoItems="infoItems"
  >
    <template #customItem>
      <CapsuleButtonGroup>
        <CapsuleButton
          text="Custom Header(센터 정렬)"
          @click="openPopup"
        />
      </CapsuleButtonGroup>

      <IconInPopup
        v-model="isOpen1"
        iconName="Bell"
        iconWidth="50"
        iconHeight="50"
        iconFixedSize="20"
        headerTitle="head"
        context="본문"
        buttonText="Icon Popup 닫기"
      />
    </template>
  </infoBoxCustom>
