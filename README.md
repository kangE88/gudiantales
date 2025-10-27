<template>
  <div class="sc-data__list">
    <!-- 타이틀 -->
    <p
      v-if="listTitle"
      class="data-list__title"
    >
      {{ listTitle }}
    </p>

    <!-- Expandable 타입 (expandedItems가 있으면) -->
    <div
      v-if="isExpandable"
      class="data-list__group py-0"
    >
      <ExpandableCard
        v-model:expanded="isExpanded"
        variant="solid"
        as="div"
        class="pa-0 bg-white"
        :label="isExpanded ? (collapseLabel || '닫기') : (expandLabel || '더보기')"
      >
        <!-- 기본 아이템들 -->
        <template
          v-for="(item, index) in items.listItems"
          :key="`default-${index}`"
        >
          <slot
            :name="`item-${index + 1}-prepend`"
            :item="item"
            :index="index"
          />
          
          <DataList :align="item.align || 'spaceBetween'">
            <template #title>
              <span class="data-list__text">{{ item.title }}</span>
              <Tooltip
                v-if="item.tooltip"
                :placement="item.tooltipPlacement || 'top-left'"
                :showClose="item.tooltipShowClose !== false"
                :content="item.tooltip"
              />
              <small v-if="item.small">{{ item.small }}</small>
            </template>
            
            <template #content>
              <!-- Toggle Switch -->
              <ToggleSwitch
                v-if="item.showSwitch"
                v-model="item.switchValue"
                :disabled="item.disabled"
              />
              
              <!-- Content Text -->
              <span
                v-if="item.content"
                class="data-list__text"
                v-html="item.content"
              />
              
              <!-- Text Button -->
              <TextButton
                v-if="item.contentBtnText"
                color="secondary"
                size="small"
                :text="item.contentBtnText"
                :rightIcon="{ iconName: 'Chevron_right' }"
                class="font-weight-300 spacing-none"
              />
              
              <!-- Box Button -->
              <div
                v-if="item.boxButtonText"
                class="data-list__btn-wrap"
              >
                <BoxButton
                  color="tertiary"
                  :text="item.boxButtonText"
                />
              </div>
            </template>
          </DataList>
          
          <slot
            :name="`item-${index + 1}-append`"
            :item="item"
            :index="index"
          />
        </template>

        <!-- 확장 영역 -->
        <template #expand>
          <DataList
            v-for="(expandedItem, index) in items.expandedItems"
            :key="`expanded-${index}`"
            :align="expandedItem.align || 'spaceBetween'"
          >
            <template #title>
              <span class="data-list__text">{{ expandedItem.title }}</span>
              <Tooltip
                v-if="expandedItem.tooltip"
                :placement="expandedItem.tooltipPlacement || 'top-left'"
                :showClose="expandedItem.tooltipShowClose !== false"
                :content="expandedItem.tooltip"
              />
              <small v-if="expandedItem.small">{{ expandedItem.small }}</small>
            </template>
            
            <template #content>
              <ToggleSwitch
                v-if="expandedItem.showSwitch"
                v-model="expandedItem.switchValue"
                :disabled="expandedItem.disabled"
              />
              
              <span
                v-if="expandedItem.content"
                class="data-list__text"
                v-html="expandedItem.content"
              />
              
              <TextButton
                v-if="expandedItem.contentBtnText"
                color="secondary"
                size="small"
                :text="expandedItem.contentBtnText"
                :rightIcon="{ iconName: 'Chevron_right' }"
                class="font-weight-300 spacing-none"
              />
            </template>
          </DataList>
        </template>
      </ExpandableCard>
    </div>

    <!-- Basic 타입 (일반 리스트) -->
    <div
      v-else
      class="data-list__group py-0"
    >
      <template
        v-for="(item, index) in items"
        :key="`basic-${index}`"
      >
        <slot
          :name="`item-${index + 1}-prepend`"
          :item="item"
          :index="index"
        />
        
        <DataList :align="item.align || 'spaceBetween'">
          <template #title>
            <span class="data-list__text">{{ item.title }}</span>
            <Tooltip
              v-if="item.tooltip"
              :placement="item.tooltipPlacement || 'top-left'"
              :showClose="item.tooltipShowClose !== false"
              :content="item.tooltip"
            />
            <small v-if="item.small">{{ item.small }}</small>
          </template>
          
          <template #content>
            <!-- Toggle Switch -->
            <ToggleSwitch
              v-if="item.showSwitch"
              v-model="item.switchValue"
              :disabled="item.disabled"
            />
            
            <!-- Content Text -->
            <span
              v-if="item.content"
              class="data-list__text"
              v-html="item.content"
            />
            
            <!-- Text Button -->
            <TextButton
              v-if="item.contentBtnText"
              color="secondary"
              size="small"
              :text="item.contentBtnText"
              :rightIcon="{ iconName: 'Chevron_right' }"
              class="font-weight-300 spacing-none"
            />
            
            <!-- Box Button -->
            <div
              v-if="item.boxButtonText"
              class="data-list__btn-wrap"
            >
              <BoxButton
                color="tertiary"
                :text="item.boxButtonText"
              />
            </div>
          </template>
        </DataList>
        
        <slot
          :name="`item-${index + 1}-append`"
          :item="item"
          :index="index"
        />
      </template>
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
import { computed, ref } from "vue";

// Props 정의
const props = defineProps({
  // 아이템 배열 또는 객체
  items: {
    type: [Array, Object],
    default: () => ([])
  },
  // 리스트 타이틀
  listTitle: {
    type: String,
    default: ""
  },
  // 확장 버튼 라벨
  expandLabel: {
    type: String,
    default: "더보기"
  },
  // 축소 버튼 라벨
  collapseLabel: {
    type: String,
    default: "닫기"
  },
  // 초기 확장 상태
  defaultExpanded: {
    type: Boolean,
    default: false
  }
});

// 확장 상태 관리
const isExpanded = ref(props.defaultExpanded);

// Expandable 타입인지 체크
// items가 객체이고 expandedItems 속성을 가지고 있으면 Expandable
const isExpandable = computed(() => {
  return props.items && 
         typeof props.items === 'object' && 
         !Array.isArray(props.items) &&
         props.items.expandedItems &&
         Array.isArray(props.items.expandedItems);
});

// Expose methods
defineExpose({
  // 확장/축소 토글
  toggle: () => {
    isExpanded.value = !isExpanded.value;
  },
  // 확장 상태 가져오기
  getExpanded: () => isExpanded.value,
  // 확장 상태 설정
  setExpanded: (value) => {
    isExpanded.value = value;
  }
});
</script>

<style scoped lang="scss">
.sc-data__list {
  width: 100%;
}
</style>
