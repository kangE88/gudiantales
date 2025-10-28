<template>
  <div class="sc-data__list">
    <!-- 타이틀 -->
    <p
      v-if="listTitle"
      class="data-list__title"
    >
      {{ listTitle }}
    </p>

    <!-- ========================================
         유형 1: Expandable (확장형)
         ======================================== -->
    <div
      v-if="isExpandable"
      class="data-list__group py-0"
    >
      <ExpandableCard
        v-model:expanded="isExpanded"
        variant="solid"
        as="div"
        class="pa-0 bg-white"
        :label="isExpanded ? collapseLabel || '닫기' : expandLabel || '더보기'"
      >
        <!-- 기본 아이템들 -->
        <template
          v-for="(item, index) in items.defaultItems"
          :key="`default-${index}`"
        >
          <DataList :align="item.align || 'spaceBetween'">
            <template #title>
              <!-- 커스텀 슬롯 우선, 없으면 기본 렌더링 -->
              <slot
                :name="`item-${index}-title`"
                :item="item"
                :index="index"
              >
                <span class="data-list__text">{{ item.title }}</span>
                <Tooltip
                  v-if="item.tooltip"
                  :placement="item.tooltipPlacement || 'top-left'"
                  :showClose="item.tooltipShowClose !== false"
                  :content="item.tooltip"
                />
                <small v-if="item.small">{{ item.small }}</small>
              </slot>
            </template>

            <template #content>
              <slot
                :name="`item-${index}-content`"
                :item="item"
                :index="index"
              >
                <ToggleSwitch
                  v-if="item.showSwitch"
                  v-model="item.switchValue"
                  :disabled="item.disabled"
                />
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
                <div
                  v-if="item.boxButtonText"
                  class="data-list__btn-wrap"
                >
                  <BoxButton
                    color="tertiary"
                    :text="item.boxButtonText"
                  />
                </div>
              </slot>
            </template>
          </DataList>
        </template>

        <!-- 확장 영역 -->
        <template #expand>
          <DataList
            v-for="(expandedItem, index) in items.expandedItems"
            :key="`expanded-${index}`"
            :align="expandedItem.align || 'spaceBetween'"
          >
            <template #title>
              <slot
                :name="`expanded-${index}-title`"
                :item="expandedItem"
                :index="index"
              >
                <span class="data-list__text">{{ expandedItem.title }}</span>
                <Tooltip
                  v-if="expandedItem.tooltip"
                  :placement="expandedItem.tooltipPlacement || 'top-left'"
                  :showClose="expandedItem.tooltipShowClose !== false"
                  :content="expandedItem.tooltip"
                />
                <small v-if="expandedItem.small">{{ expandedItem.small }}</small>
              </slot>
            </template>

            <template #content>
              <slot
                :name="`expanded-${index}-content`"
                :item="expandedItem"
                :index="index"
              >
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
              </slot>
            </template>
          </DataList>
        </template>
      </ExpandableCard>
    </div>

    <!-- ========================================
         유형 2: Box (Gray 단일 박스)
         ======================================== -->
    <BasicCard
      v-else-if="isBox"
      variant="solid"
      color="gray"
    >
      <div class="data-list__group">
        <DataList
          v-for="(boxItem, index) in items.boxItems"
          :key="`box-${index}`"
          :align="boxItem.align || 'spaceBetween'"
        >
          <template #title>
            <slot
              :name="`box-${index}-title`"
              :item="boxItem"
              :index="index"
            >
              <span class="data-list__text">{{ boxItem.title }}</span>
              <Tooltip
                v-if="boxItem.tooltip"
                :placement="boxItem.tooltipPlacement || 'top-left'"
                :showClose="boxItem.tooltipShowClose !== false"
                :content="boxItem.tooltip"
              />
            </slot>
          </template>
          <template #content>
            <slot
              :name="`box-${index}-content`"
              :item="boxItem"
              :index="index"
            >
              <span
                v-if="boxItem.content"
                class="data-list__text"
              >{{ boxItem.content }}</span>
            </slot>
          </template>
        </DataList>
      </div>
    </BasicCard>

    <!-- ========================================
         유형 3: BoxInBox (외부 카드 + 내부 Gray 카드)
         ======================================== -->
    <BasicCard
      v-else-if="isBoxInBox"
      variant="outline"
    >
      <!-- 상단 타이틀 -->
      <h3
        v-if="items.title"
        class="data-list__title"
      >
        {{ items.title }}
      </h3>

      <!-- Top Items (Gray 카드) -->
      <BasicCard
        v-if="items.topItems && items.topItems.length"
        variant="solid"
        color="gray"
      >
        <div class="data-list__group">
          <DataList
            v-for="(topItem, index) in items.topItems"
            :key="`top-${index}`"
            :align="topItem.align || 'spaceBetween'"
          >
            <template #title>
              <slot
                :name="`top-${index}-title`"
                :item="topItem"
                :index="index"
              >
                <span class="data-list__text">{{ topItem.title }}</span>
                <Tooltip
                  v-if="topItem.tooltip"
                  :placement="topItem.tooltipPlacement || 'top-left'"
                  :showClose="topItem.tooltipShowClose !== false"
                  :content="topItem.tooltip"
                />
              </slot>
            </template>
            <template #content>
              <slot
                :name="`top-${index}-content`"
                :item="topItem"
                :index="index"
              >
                <span
                  v-if="topItem.content"
                  class="data-list__text"
                >{{ topItem.content }}</span>
              </slot>
            </template>
          </DataList>
        </div>
      </BasicCard>

      <!-- Main Items (중간 일반 영역) -->
      <div
        v-if="items.mainItems && items.mainItems.length"
        class="data-list__group"
      >
        <DataList
          v-for="(mainItem, index) in items.mainItems"
          :key="`main-${index}`"
          :align="mainItem.align || 'spaceBetween'"
        >
          <template #title>
            <slot
              :name="`main-${index}-title`"
              :item="mainItem"
              :index="index"
            >
              <span class="data-list__text">{{ mainItem.title }}</span>
              <Tooltip
                v-if="mainItem.tooltip"
                :placement="mainItem.tooltipPlacement || 'top-left'"
                :showClose="mainItem.tooltipShowClose !== false"
                :content="mainItem.tooltip"
              />
            </slot>
          </template>
          <template #content>
            <slot
              :name="`main-${index}-content`"
              :item="mainItem"
              :index="index"
            >
              <span
                v-if="mainItem.content"
                class="data-list__text"
              >{{ mainItem.content }}</span>
            </slot>
          </template>
        </DataList>
      </div>

      <!-- Bottom Items (하단 일반 영역) -->
      <div
        v-if="items.bottomItems && items.bottomItems.length"
        class="data-list__group"
      >
        <DataList
          v-for="(bottomItem, index) in items.bottomItems"
          :key="`bottom-${index}`"
          :align="bottomItem.align || 'spaceBetween'"
        >
          <template #title>
            <slot
              :name="`bottom-${index}-title`"
              :item="bottomItem"
              :index="index"
            >
              <span class="data-list__text">{{ bottomItem.title }}</span>
              <Tooltip
                v-if="bottomItem.tooltip"
                :placement="bottomItem.tooltipPlacement || 'top-left'"
                :showClose="bottomItem.tooltipShowClose !== false"
                :content="bottomItem.tooltip"
              />
            </slot>
          </template>
          <template #content>
            <slot
              :name="`bottom-${index}-content`"
              :item="bottomItem"
              :index="index"
            >
              <span
                v-if="bottomItem.content"
                class="data-list__text"
              >{{ bottomItem.content }}</span>
            </slot>
          </template>
        </DataList>
      </div>

      <!-- Actions 슬롯 -->
      <template #actions>
        <slot name="actions" />
      </template>
    </BasicCard>

    <!-- ========================================
         유형 0: Basic (기본 배열)
         ======================================== -->
    <div
      v-else
      class="data-list__group py-0"
    >
      <template
        v-for="(item, index) in items"
        :key="`basic-${index}`"
      >
        <slot
          :name="`item-${index}-prepend`"
          :item="item"
          :index="index"
        />

        <DataList :align="item.align || 'spaceBetween'">
          <template #title>
            <slot
              :name="`item-${index}-title`"
              :item="item"
              :index="index"
            >
              <span class="data-list__text">{{ item.title }}</span>
              <Tooltip
                v-if="item.tooltip"
                :placement="item.tooltipPlacement || 'top-left'"
                :showClose="item.tooltipShowClose !== false"
                :content="item.tooltip"
              />
              <small v-if="item.small">{{ item.small }}</small>
            </slot>
          </template>

          <template #content>
            <slot
              :name="`item-${index}-content`"
              :item="item"
              :index="index"
            >
              <ToggleSwitch
                v-if="item.showSwitch"
                v-model="item.switchValue"
                :disabled="item.disabled"
              />
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
              <div
                v-if="item.boxButtonText"
                class="data-list__btn-wrap"
              >
                <BoxButton
                  color="tertiary"
                  :text="item.boxButtonText"
                />
              </div>
            </slot>
          </template>
        </DataList>

        <slot
          :name="`item-${index}-append`"
          :item="item"
          :index="index"
        />
      </template>
    </div>
  </div>
</template>

<script setup>
import {
  BasicCard,
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
    default: () => [],
  },
  // 리스트 타이틀
  listTitle: {
    type: String,
    default: "",
  },
  // 확장 버튼 라벨
  expandLabel: {
    type: String,
    default: "더보기",
  },
  // 축소 버튼 라벨
  collapseLabel: {
    type: String,
    default: "닫기",
  },
  // 초기 확장 상태
  defaultExpanded: {
    type: Boolean,
    default: false,
  },
});

// 확장 상태 관리
const isExpanded = ref(props.defaultExpanded);

// ========================================
// 유형 자동 감지
// ========================================

// 유형 1: Expandable (확장형)
// expandedItems 속성이 있으면 활성화
const isExpandable = computed(() => {
  return (
    typeof props.items === "object" &&
    !Array.isArray(props.items) &&
    props.items.expandedItems &&
    Array.isArray(props.items.expandedItems)
  );
});

// 유형 2: Box (단일 Gray 박스)
// boxItems 속성이 있으면 활성화
const isBox = computed(() => {
  return (
    typeof props.items === "object" &&
    !Array.isArray(props.items) &&
    props.items.boxItems &&
    Array.isArray(props.items.boxItems) &&
    !props.items.topItems &&
    !props.items.bottomItems
  );
});

// 유형 3: BoxInBox (박스 안에 박스)
// topItems 또는 bottomItems가 있으면 활성화
const isBoxInBox = computed(() => {
  return (
    typeof props.items === "object" &&
    !Array.isArray(props.items) &&
    (props.items.topItems || props.items.bottomItems || props.items.mainItems)
  );
});

// 디버그 로그
if (import.meta.env.DEV) {
  console.log("ScDataList 유형 감지:", {
    isExpandable: isExpandable.value,
    isBox: isBox.value,
    isBoxInBox: isBoxInBox.value,
    items: props.items,
  });
}

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
  },
  // 유형 정보 가져오기
  getType: () => {
    if (isExpandable.value) return "expandable";
    if (isBoxInBox.value) return "boxInBox";
    if (isBox.value) return "box";
    return "basic";
  },
});
</script>

<style scoped lang="scss">
.sc-data__list {
  width: 100%;
}
</style>
