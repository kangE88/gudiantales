effect?: EffectType | (string & {});
<template>
  <div :class="containerClasses">
    <!-- Screen Reader용 Live Region -->
    <div
      v-if="announceChanges"
      :id="`${componentId}-live-region`"
      aria-live="polite"
      aria-atomic="true"
      class="sr-only"
    >
      {{ liveRegionText }}
    </div>

    <!-- Wrapper Card (variant가 box 또는 boxInBox일 때) -->
    <component
      :is="wrapperComponent"
      v-if="needsWrapperCard"
      v-bind="wrapperCardProps"
    >
      <!-- Title (variant가 boxInBox일 때만 카드 내부 상단에 표시) -->
      <h3
        v-if="title && variant === 'boxInBox'"
      class="data-list__title"
    >
      {{ title }}
      </h3>

      <!-- Top Group (boxInBox의 gray 영역) -->
      <component
        :is="innerCardComponent"
        v-if="variant === 'boxInBox' && topGroupItems?.length"
        v-bind="innerCardProps"
      >
        <div class="data-list__group">
          <DataList
            v-for="(item, index) in topGroupItems"
            :key="`top-${index}`"
            :align="item.align || align"
          >
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
              <ToggleSwitch
                v-if="item.showSwitch"
                :modelValue="item.switchValue"
                :disabled="item.disabled"
                @update:modelValue="(value) => onSwitchChange(index, value, 'top')"
              />
              <span
                v-if="item.content"
                class="data-list__text"
                :class="{ 'text-clickable': item.contentClickable }"
                @click="item.contentClickable && onContentClick(index, 'top', $event)"
                >{{ item.content }}</span
              >
              <TextButton
                v-if="item.contentBtnText"
                color="secondary"
                size="small"
                :text="item.contentBtnText"
                :rightIcon="{ iconName: 'Chevron_right' }"
                class="font-weight-300 spacing-none"
                @click="onButtonClick(index, 'top', $event)"
              />
              <slot
                :name="`top-item-${index + 1}-content`"
                :item="item"
                :index="index"
              />
            </template>
          </DataList>
        </div>
      </component>

      <!-- Main Group -->
      <div class="data-list__group">
      <template
        v-for="(item, index) in items"
          :key="`main-${index}`"
        >
          <slot
            :name="`item-${index + 1}-prepend`"
            :item="item"
            :index="index"
          />
          <DataList
            :align="item.align || align"
            :tabindex="clickable ? 0 : undefined"
            @click="clickable && onItemClick(index, $event)"
            @keydown="clickable && onItemKeydown($event, index)"
          >
            <template #title>
              <span class="data-list__text">{{ item.title }}</span>
              <Tooltip
                v-if="item.tooltip"
                :placement="item.tooltipPlacement || 'top-left'"
                :showClose="item.tooltipShowClose !== false"
                :content="item.tooltip"
              />
              <small v-if="item.small">{{ item.small }}</small>
              <slot
                :name="`item-${index + 1}-title`"
                :item="item"
                :index="index"
              />
            </template>
            <template #content>
              <ToggleSwitch
                v-if="item.showSwitch"
                :modelValue="item.switchValue"
                :disabled="item.disabled"
                @update:modelValue="(value) => onSwitchChange(index, value, 'main')"
              />
              <span
                v-if="item.content"
                class="data-list__text"
                :class="{ 'text-clickable': item.contentClickable }"
                @click="item.contentClickable && onContentClick(index, 'main', $event)"
                >{{ item.content }}</span
              >
              <TextButton
                v-if="item.contentBtnText"
                color="secondary"
                size="small"
                :text="item.contentBtnText"
                :rightIcon="{ iconName: 'Chevron_right' }"
                class="font-weight-300 spacing-none"
                @click="onButtonClick(index, 'main', $event)"
              />
              <slot
                :name="`item-${index + 1}-content`"
                :item="item"
                :index="index"
              />
            </template>
          </DataList>
          <slot
            :name="`item-${index + 1}-append`"
            :item="item"
            :index="index"
          />
        </template>
      </div>

      <!-- Bottom Group (boxInBox의 일반 영역) -->
      <div
        v-if="variant === 'boxInBox' && bottomGroupItems?.length"
        class="data-list__group"
      >
        <DataList
          v-for="(item, index) in bottomGroupItems"
          :key="`bottom-${index}`"
          :align="item.align || align"
        >
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
            <ToggleSwitch
              v-if="item.showSwitch"
              :modelValue="item.switchValue"
              :disabled="item.disabled"
              @update:modelValue="(value) => onSwitchChange(index, value, 'bottom')"
            />
            <span
              v-if="item.content"
              class="data-list__text"
              :class="{ 'text-clickable': item.contentClickable }"
              @click="item.contentClickable && onContentClick(index, 'bottom', $event)"
              >{{ item.content }}</span
            >
            <TextButton
              v-if="item.contentBtnText"
              color="secondary"
              size="small"
              :text="item.contentBtnText"
              :rightIcon="{ iconName: 'Chevron_right' }"
              class="font-weight-300 spacing-none"
              @click="onButtonClick(index, 'bottom', $event)"
            />
            <slot
              :name="`bottom-item-${index + 1}-content`"
              :item="item"
              :index="index"
            />
          </template>
        </DataList>
      </div>

      <!-- Actions Slot (boxInBox의 하단 버튼 영역) -->
      <template
        v-if="variant === 'boxInBox'"
        #actions
      >
        <slot name="actions" />
      </template>
    </component>

    <!-- No Card Wrapper (variant가 basic일 때) -->
    <template v-else>
      <!-- Title (variant가 basic일 때만 외부에 표시) -->
      <p
        v-if="title"
        class="data-list__title"
        role="heading"
        :aria-level="titleLevel"
      >
        {{ title }}
      </p>

      <div
        class="data-list__group"
        :class="groupClasses"
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
          <DataList
            :align="item.align || align"
            :tabindex="clickable ? 0 : undefined"
            @click="clickable && onItemClick(index, $event)"
            @keydown="clickable && onItemKeydown($event, index)"
          >
            <template #title>
              <span class="data-list__text">{{ item.title }}</span>
              <Tooltip
                v-if="item.tooltip"
                :placement="item.tooltipPlacement || 'top-left'"
                :showClose="item.tooltipShowClose !== false"
                :content="item.tooltip"
              />
              <small v-if="item.small">{{ item.small }}</small>
              <slot
                :name="`item-${index + 1}-title`"
                :item="item"
                :index="index"
              />
            </template>
            <template #content>
              <ToggleSwitch
                v-if="item.showSwitch"
                :modelValue="item.switchValue"
                :disabled="item.disabled"
                @update:modelValue="(value) => onSwitchChange(index, value, 'main')"
              />
              <span
                v-if="item.content"
                class="data-list__text"
                :class="{ 'text-clickable': item.contentClickable }"
                @click="item.contentClickable && onContentClick(index, 'main', $event)"
                >{{ item.content }}</span
              >
              <TextButton
                v-if="item.contentBtnText"
                color="secondary"
                size="small"
                :text="item.contentBtnText"
                :rightIcon="{ iconName: 'Chevron_right' }"
                class="font-weight-300 spacing-none"
                @click="onButtonClick(index, 'main', $event)"
              />
              <slot
                :name="`item-${index + 1}-content`"
                :item="item"
                :index="index"
              />
            </template>
          </DataList>
          <slot
            :name="`item-${index + 1}-append`"
            :item="item"
            :index="index"
          />
      </template>
    </div>
    </template>
  </div>
</template>

<script setup lang="ts">
// ============================================================================
// IMPORTS
// ============================================================================
import { BasicCard, DataList, TextButton, ToggleSwitch, Tooltip } from "@shc-nss/ui/solid";
import { computed, ref } from "vue";

// ============================================================================
// TYPES & INTERFACES
// ============================================================================
export interface ScDataListItem {
  /** 제목 텍스트 */
  title: string;
  /** 툴팁 내용 */
  tooltip?: string;
  /** 툴팁 위치 */
  tooltipPlacement?: "top-left" | "top-center" | "top-right" | "bottom-left" | "bottom-center" | "bottom-right";
  /** 툴팁 닫기 버튼 표시 여부 */
  tooltipShowClose?: boolean;
  /** 작은 텍스트 (타이틀 옆에 표시) */
  small?: string;
  /** 콘텐츠 텍스트 */
  content?: string;
  /** 콘텐츠 클릭 가능 여부 */
  contentClickable?: boolean;
  /** 버튼 텍스트 */
  contentBtnText?: string;
  /** 스위치 표시 여부 */
  showSwitch?: boolean;
  /** 스위치 값 */
  switchValue?: boolean;
  /** 비활성화 여부 */
  disabled?: boolean;
  /** 개별 정렬 방식 (전체 align을 오버라이드) */
  align?: "spaceBetween" | "left" | "center" | "right";
}

export interface ScDataListProps {
  /** 메인 아이템 목록 */
  items?: ScDataListItem[];
  /** 타이틀 */
  title?: string;
  /** 타이틀 레벨 (heading level) */
  titleLevel?: number;
  /** 레이아웃 유형 */
  variant?: "basic" | "box" | "boxInBox";
  /** 정렬 방식 */
  align?: "spaceBetween" | "left" | "center" | "right";
  /** 조밀한 스타일 */
  dense?: boolean;
  /** 구분선 표시 여부 */
  divider?: boolean;
  /** 클릭 가능 여부 */
  clickable?: boolean;
  /** 외부 카드 variant (box, boxInBox일 때) */
  cardVariant?: "outline" | "solid" | "elevated";
  /** 외부 카드 색상 */
  cardColor?: "default" | "gray" | "primary" | "secondary";
  /** 내부 카드 variant (boxInBox일 때) */
  innerCardVariant?: "outline" | "solid" | "elevated";
  /** 내부 카드 색상 (boxInBox일 때) */
  innerCardColor?: "default" | "gray" | "primary" | "secondary";
  /** Top 그룹 아이템 (boxInBox 전용) */
  topGroupItems?: ScDataListItem[];
  /** Bottom 그룹 아이템 (boxInBox 전용) */
  bottomGroupItems?: ScDataListItem[];
  /** 패딩 제거 */
  noPadding?: boolean;
  /** 접근성: 변경사항 음성 안내 */
  announceChanges?: boolean;
  /** 접근성: 커스텀 aria-label */
  ariaLabel?: string;
}

// ============================================================================
// UTILITIES
// ============================================================================
let idCounter = 0;
const generateId = () => `sc-datalist-${++idCounter}-${Date.now()}`;

// ============================================================================
// COMPONENT SETUP
// ============================================================================
const props = withDefaults(defineProps<ScDataListProps>(), {
  items: () => [],
  titleLevel: 2,
  variant: "basic",
  align: "spaceBetween",
  dense: false,
  divider: true,
  clickable: false,
  cardVariant: "outline",
  cardColor: "default",
  innerCardVariant: "solid",
  innerCardColor: "gray",
  topGroupItems: () => [],
  bottomGroupItems: () => [],
  noPadding: false,
  announceChanges: true,
});

// Emit 타입 정의
type SwitchChangePayload = {
  index: number;
  value: boolean;
  item: ScDataListItem;
  group: "top" | "main" | "bottom";
};

type ButtonClickPayload = {
  index: number;
  item: ScDataListItem;
  event: Event;
  group: "top" | "main" | "bottom";
};

type ItemClickPayload = {
  index: number;
  item: ScDataListItem;
  event: Event;
};

type ContentClickPayload = {
  index: number;
  item: ScDataListItem;
  event: Event;
  group: "top" | "main" | "bottom";
};

const emit = defineEmits<{
  "switch-change": [payload: SwitchChangePayload];
  "button-click": [payload: ButtonClickPayload];
  "item-click": [payload: ItemClickPayload];
  "content-click": [payload: ContentClickPayload];
}>();

// ============================================================================
// STATE
// ============================================================================
const componentId = ref(generateId());
const liveRegionText = ref("");
const focusedIndex = ref(-1);

// ============================================================================
// COMPUTED
// ============================================================================
const containerClasses = computed(() => {
  return [
    "sc-data__list",
    props.dense && "sc-data__list--dense",
    props.clickable && "sc-data__list--clickable",
    props.noPadding && "sc-data__list--no-padding",
  ].filter(Boolean);
});

const groupClasses = computed(() => {
  return [props.noPadding && "py-0"].filter(Boolean);
});

const needsWrapperCard = computed(() => {
  return props.variant === "box" || props.variant === "boxInBox";
});

const wrapperComponent = computed(() => BasicCard);
const innerCardComponent = computed(() => BasicCard);

const wrapperCardProps = computed(() => {
  if (props.variant === "box") {
    return {
      variant: props.cardVariant === "outline" ? "solid" : props.cardVariant,
      color: props.cardColor === "default" ? "gray" : props.cardColor,
    };
  }
  if (props.variant === "boxInBox") {
    return {
      variant: props.cardVariant,
      color: props.cardColor,
    };
  }
  return {};
});

const innerCardProps = computed(() => {
  return {
    variant: props.innerCardVariant,
    color: props.innerCardColor,
  };
});

const accessibilityLabel = computed(() => {
  return props.ariaLabel || props.title || "데이터 목록";
});

// ============================================================================
// ACCESSIBILITY METHODS
// ============================================================================
const updateLiveRegion = (message: string) => {
  if (props.announceChanges) {
    liveRegionText.value = message;
    setTimeout(() => {
      liveRegionText.value = "";
    }, 1000);
  }
};

// ============================================================================
// EVENT HANDLERS
// ============================================================================
const onSwitchChange = (
  index: number,
  value: boolean,
  group: "top" | "main" | "bottom"
) => {
  let item: ScDataListItem | undefined;
  
  if (group === "top") {
    item = props.topGroupItems[index];
  } else if (group === "main") {
    item = props.items[index];
  } else {
    item = props.bottomGroupItems[index];
  }

  if (!item) return;

  emit("switch-change", { index, value, item, group });

  if (props.announceChanges) {
    updateLiveRegion(`${item.title} ${value ? "활성화됨" : "비활성화됨"}`);
  }
};

const onButtonClick = (
  index: number,
  group: "top" | "main" | "bottom",
  event: Event
) => {
  let item: ScDataListItem | undefined;
  
  if (group === "top") {
    item = props.topGroupItems[index];
  } else if (group === "main") {
    item = props.items[index];
  } else {
    item = props.bottomGroupItems[index];
  }

  if (!item) return;

  emit("button-click", { index, item, event, group });
};

const onItemClick = (index: number, event: Event) => {
  if (!props.clickable) return;
  const item = props.items[index];
  emit("item-click", { index, item, event });
};

const onContentClick = (
  index: number,
  group: "top" | "main" | "bottom",
  event: Event
) => {
  let item: ScDataListItem | undefined;
  
  if (group === "top") {
    item = props.topGroupItems[index];
  } else if (group === "main") {
    item = props.items[index];
  } else {
    item = props.bottomGroupItems[index];
  }

  if (!item) return;

  emit("content-click", { index, item, event, group });
};

const onItemKeydown = (event: KeyboardEvent, index: number) => {
  const { key } = event;

  if (key === "Enter" || key === " ") {
    event.preventDefault();
    onItemClick(index, event);
  } else if (key === "ArrowDown") {
    event.preventDefault();
    focusNextItem(index);
  } else if (key === "ArrowUp") {
    event.preventDefault();
    focusPrevItem(index);
  }
};

const focusNextItem = (currentIndex: number) => {
  const nextIndex = Math.min(currentIndex + 1, props.items.length - 1);
  focusedIndex.value = nextIndex;
  // TODO: 실제 DOM 요소에 포커스 이동 구현
};

const focusPrevItem = (currentIndex: number) => {
  const prevIndex = Math.max(currentIndex - 1, 0);
  focusedIndex.value = prevIndex;
  // TODO: 실제 DOM 요소에 포커스 이동 구현
};

// ============================================================================
// EXPOSE
// ============================================================================
defineExpose({
  /** 모든 아이템 가져오기 */
  getItems: () => props.items,
  /** 특정 인덱스의 아이템 가져오기 */
  getItemByIndex: (index: number) => props.items?.[index],
  /** Top 그룹 아이템 가져오기 */
  getTopGroupItems: () => props.topGroupItems,
  /** Bottom 그룹 아이템 가져오기 */
  getBottomGroupItems: () => props.bottomGroupItems,
  /** 특정 아이템에 포커스 */
  focusItem: (index: number) => {
    focusedIndex.value = index;
  },
  /** Live Region 업데이트 (접근성) */
  announce: (message: string) => updateLiveRegion(message),
});
</script>

<style scoped lang="scss">
@use "@assets/styles/module/_data-list" as *; // 모듈 영역 스타일
</style>
