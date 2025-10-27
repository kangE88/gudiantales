<template>
  <div class="sc-data__list">
    <p
      v-if="listTitle"
      class="data-list__title"
    >
      {{ listTitle }}
    </p>

    <div class="data-list__group py-0">
      <!-- {{ items.listItem }}
      {{ items.expandedItems }} -->
      <ExpandableCard
        v-model:expanded="props.expanded"
        variant="solid"
        @click="toggleExpanded"
        onContentClick=""
        as="div"
        class="pa-0 bg-white"
        :label="expanded ? '닫기' : '더보기'"
        v-if="items.expandedItems"
      >
        <DataList v-for="(list, i) in items.listItems">
          <template #title>
            {{ list.title }}
          </template>
          <template #content>
            {{ list.content }}
          </template>
        </DataList>
        <template #expand>
          <DataList v-for="(expended, i) in items.expandedItems">
            <template #title>
              {{ expended.title }}
            </template>
            <template #content>
              {{ expended.content }}
            </template>
          </DataList>
        </template>
      </ExpandableCard>
    </div>
    <!--basic-->
    <div
      class="data-list__group py-0"
      v-if="!items.listItem"
    >
      <DataList
        align="spaceBetween"
        v-for="(item, index) in items"
      >
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
          <!--Option 1 - Toggle Switch -->
          <ToggleSwitch v-if="item.showSwitch" />
          <span
            v-if="item.content"
            class="data-list__text"
            v-html="item.content"
          />
          <!--Option 2 - TextButton -->
          <TextButton
            v-if="item.contentBtnText"
            color="secondary"
            size="small"
            :text="item.contentBtnText"
            :rightIcon="{ iconName: 'Chevron_right' }"
            class="font-weight-300 spacing-none"
          />
          <!--Option 3 - BoxButton -->
          <div
            class="data-list__btn-wrap"
            v-if="item.boxButtonText"
          >
            <BoxButton
              color="tertiary"
              text="메모 입력하기"
            />
          </div>
        </template>
      </DataList>
    </div>
  </div>
</template>

<script setup>
import {
  BoxButton,
  DataList,
  ExpandableCard,
  TextButton,
  ToggleSwitch,
  Tooltip,
} from "@shc-nss/ui/solid";
import { onMounted } from "vue";

const props = defineProps({
  items: {
    type: Array,
  },
  listTitle: {
    type: String,
  },
  expanded: {
    type: Boolean,
    default: false,
  },
});

const toggleExpanded = () => {
  props.expanded = !props.expanded;
};

onMounted(() => {
  console.log("expanded>>>", props.expanded);
});
// const expanded = defineModel(props.expanded);
</script>
