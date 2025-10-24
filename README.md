<template>
  <div class="sc-data__list">
    <p
      v-if="title"
      class="data-list__title"
    >
      {{ title }}
    </p>
    <div class="data-list__group py-0">
      <template
        v-for="(item, index) in items"
        :key="index"
      >
        <slot :name="'item-' + (index + 1) + '-prepend'"></slot>
        <DataList align="spaceBetween">
          <template #title>
            <span class="data-list__text">{{ item.title }}</span>
            <Tooltip
              v-if="item.tooltip"
              placement="top-left"
              :showClose="true"
              :content="item.tooltip"
            />
          </template>
          <template #content>
            <!-- 다른형식도 추가 될 가능성이 보인다.-->
            <ToggleSwitch v-if="item.showSwitch" />
            <span
              v-if="item.content"
              class="data-list__text"
              v-html="item.content"
            />
            <TextButton
              v-if="item.contentBtnText"
              color="secondary"
              size="small"
              :text="item.contentBtnText"
              :rightIcon="{ iconName: 'Chevron_right' }"
              class="font-weight-300 spacing-none"
            />
          </template>
        </DataList>
        <slot :name="'item-' + (index + 1) + '-append'"></slot>
      </template>
    </div>
  </div>
</template>

<script setup>
import { DataList, TextButton, ToggleSwitch, Tooltip } from "@shc-nss/ui/solid";
const props = defineProps({
  items: {
    type: Array,
  },
  title: {
    type: String,
  },
});
</script>
