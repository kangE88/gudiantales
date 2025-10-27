
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

    <!-- 유형별 디버그 정보 (개발 모드) -->
    <div
      v-if="debug"
      class="sc-data__debug"
    >
      <strong>감지된 타입:</strong> {{ detectedType }}
      <span v-if="hasMultipleTypes"> (복합 유형)</span>
    </div>

    <!-- ========================================================================
         유형 1: Expandable Type (확장형)
         우선순위: 1순위 (expandedItems가 있으면 무조건 이 타입)
         ======================================================================== -->
    <div
      v-if="isExpandable"
      class="expandable-data-list"
    >
      <!-- Expandable Header 슬롯 -->
      <slot
        name="expandable-header"
        :expanded="expanded"
        :toggle="toggleExpanded"
      />

      <ExpandableCard
        v-model:expanded="expanded"
        variant="solid"
        as="div"
        class="pa-0 bg-white"
        :label="expanded ? (data.collapseLabel || '닫기') : (data.expandLabel || '더보기')"
      >
        <!-- Default Items -->
        <div class="data-list__group">
          <template
            v-for="(item, index) in data.items"
            :key="`default-${index}`"
          >
            <slot
              :name="`item-${index + 1}-prepend`"
              :item="item"
              :index="index"
              :type="'expandable'"
            />
            <DataList
              :align="item.align || computedAlign"
              :tabindex="clickable ? 0 : undefined"
              @click="clickable && onItemClick(index, $event, 'default')"
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
                  :type="'expandable'"
                />
              </template>
              <template #content>
                <ToggleSwitch
                  v-if="item.showSwitch"
                  :modelValue="item.switchValue"
                  :disabled="item.disabled"
                  @update:modelValue="(value) => onSwitchChange(index, value, 'default')"
                />
                <span
                  v-if="item.content"
                  class="data-list__text"
                  :class="{ 'text-clickable': item.contentClickable }"
                  @click="item.contentClickable && onContentClick(index, 'default', $event)"
                  >{{ item.content }}</span
                >
                <TextButton
                  v-if="item.contentBtnText"
                  color="secondary"
                  size="small"
                  :text="item.contentBtnText"
                  :rightIcon="{ iconName: 'Chevron_right' }"
                  class="font-weight-300 spacing-none"
                  @click="onButtonClick(index, 'default', $event)"
                />
                <slot
                  :name="`item-${index + 1}-content`"
                  :item="item"
                  :index="index"
                  :type="'expandable'"
                />
              </template>
            </DataList>
            <slot
              :name="`item-${index + 1}-append`"
              :item="item"
              :index="index"
              :type="'expandable'"
            />
          </template>
        </div>

        <!-- Expanded Items -->
        <template #expand>
          <!-- Expanded Header 슬롯 -->
          <slot name="expandable-expanded-header" />

          <div class="data-list__group">
            <DataList
              v-for="(item, index) in data.expandedItems"
              :key="`expanded-${index}`"
              :align="item.align || computedAlign"
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
                  :name="`expanded-item-${index + 1}-title`"
                  :item="item"
                  :index="index"
                />
              </template>
              <template #content>
                <ToggleSwitch
                  v-if="item.showSwitch"
                  :modelValue="item.switchValue"
                  :disabled="item.disabled"
                  @update:modelValue="(value) => onSwitchChange(index, value, 'expanded')"
                />
                <span
                  v-if="item.content"
                  class="data-list__text"
                  :class="{ 'text-clickable': item.contentClickable }"
                  @click="item.contentClickable && onContentClick(index, 'expanded', $event)"
                  >{{ item.content }}</span
                >
                <TextButton
                  v-if="item.contentBtnText"
                  color="secondary"
                  size="small"
                  :text="item.contentBtnText"
                  :rightIcon="{ iconName: 'Chevron_right' }"
                  class="font-weight-300 spacing-none"
                  @click="onButtonClick(index, 'expanded', $event)"
                />
                <slot
                  :name="`expanded-item-${index + 1}-content`"
                  :item="item"
                  :index="index"
                />
              </template>
            </DataList>
          </div>

          <!-- Expanded Footer 슬롯 -->
          <slot name="expandable-expanded-footer" />
        </template>
      </ExpandableCard>

      <!-- Expandable Footer 슬롯 -->
      <slot
        name="expandable-footer"
        :expanded="expanded"
        :toggle="toggleExpanded"
      />
    </div>

    <!-- ========================================================================
         유형 2 & 3: Box / BoxInBox Type (카드가 필요한 경우)
         우선순위: 2순위 (BoxInBox), 3순위 (Box)
         ======================================================================== -->
    <component
      :is="BasicCard"
      v-else-if="needsCard"
      v-bind="cardProps"
    >
      <!-- BoxInBox Header 슬롯 (title 전) -->
      <slot
        v-if="isBoxInBox"
        name="boxInBox-header"
        :data="data"
      />

      <!-- Box Header 슬롯 -->
      <slot
        v-else-if="isBox"
        name="box-header"
        :data="data"
      />

      <!-- Title (boxInBox 타입일 때만 카드 내부 상단에 표시) -->
      <h3
        v-if="data.title && isBoxInBox"
        class="data-list__title"
      >
        {{ data.title }}
      </h3>

      <!-- Top Group (boxInBox의 gray 영역) -->
      <component
        :is="BasicCard"
        v-if="isBoxInBox && data.topItems?.length"
        v-bind="innerCardProps"
      >
        <!-- Top Group Header 슬롯 -->
        <slot name="boxInBox-top-header" />

        <div class="data-list__group">
          <DataList
            v-for="(item, index) in data.topItems"
            :key="`top-${index}`"
            :align="item.align || computedAlign"
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
                :name="`top-item-${index + 1}-title`"
                :item="item"
                :index="index"
              />
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

        <!-- Top Group Footer 슬롯 -->
        <slot name="boxInBox-top-footer" />
      </component>

      <!-- BoxInBox Between 슬롯 (Top과 Main 사이) -->
      <slot
        v-if="isBoxInBox && data.topItems?.length && data.items?.length"
        name="boxInBox-between"
      />

      <!-- Main Group -->
      <div
        v-if="data.items?.length"
        class="data-list__group"
      >
        <!-- Main Group Header 슬롯 -->
        <slot
          :name="isBoxInBox ? 'boxInBox-main-header' : 'box-main-header'"
        />

        <template
          v-for="(item, index) in data.items"
          :key="`main-${index}`"
        >
          <slot
            :name="`item-${index + 1}-prepend`"
            :item="item"
            :index="index"
            :type="isBoxInBox ? 'boxInBox' : 'box'"
          />
          <DataList
            :align="item.align || computedAlign"
            :tabindex="clickable ? 0 : undefined"
            @click="clickable && onItemClick(index, $event, 'main')"
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
                :type="isBoxInBox ? 'boxInBox' : 'box'"
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
                :type="isBoxInBox ? 'boxInBox' : 'box'"
              />
            </template>
          </DataList>
          <slot
            :name="`item-${index + 1}-append`"
            :item="item"
            :index="index"
            :type="isBoxInBox ? 'boxInBox' : 'box'"
          />
        </template>

        <!-- Main Group Footer 슬롯 -->
        <slot
          :name="isBoxInBox ? 'boxInBox-main-footer' : 'box-main-footer'"
        />
      </div>

      <!-- BoxInBox Between2 슬롯 (Main과 Bottom 사이) -->
      <slot
        v-if="isBoxInBox && data.items?.length && data.bottomItems?.length"
        name="boxInBox-between2"
      />

      <!-- Bottom Group (boxInBox의 일반 영역) -->
      <div
        v-if="isBoxInBox && data.bottomItems?.length"
        class="data-list__group"
      >
        <!-- Bottom Group Header 슬롯 -->
        <slot name="boxInBox-bottom-header" />

        <DataList
          v-for="(item, index) in data.bottomItems"
          :key="`bottom-${index}`"
          :align="item.align || computedAlign"
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
              :name="`bottom-item-${index + 1}-title`"
              :item="item"
              :index="index"
            />
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

        <!-- Bottom Group Footer 슬롯 -->
        <slot name="boxInBox-bottom-footer" />
      </div>

      <!-- Actions Slot (boxInBox의 하단 버튼 영역) -->
      <template
        v-if="isBoxInBox"
        #actions
      >
        <slot name="actions" />
      </template>

      <!-- BoxInBox Footer 슬롯 -->
      <slot
        v-if="isBoxInBox"
        name="boxInBox-footer"
        :data="data"
      />

      <!-- Box Footer 슬롯 -->
      <slot
        v-else-if="isBox"
        name="box-footer"
        :data="data"
      />
    </component>

    <!-- ========================================================================
         유형 4: Basic Type (카드 없음)
         우선순위: 4순위 (마지막)
         ======================================================================== -->
    <template v-else>
      <!-- Basic Header 슬롯 -->
      <slot
        name="basic-header"
        :data="data"
      />

      <!-- Title (basic 타입일 때만 외부에 표시) -->
      <p
        v-if="data.title"
        class="data-list__title"
        role="heading"
        :aria-level="titleLevel"
      >
        {{ data.title }}
      </p>

      <div
        class="data-list__group"
        :class="groupClasses"
      >
        <template
          v-for="(item, index) in data.items"
          :key="`basic-${index}`"
        >
          <slot
            :name="`item-${index + 1}-prepend`"
            :item="item"
            :index="index"
            :type="'basic'"
          />
          <DataList
            :align="item.align || computedAlign"
            :tabindex="clickable ? 0 : undefined"
            @click="clickable && onItemClick(index, $event, 'main')"
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
                :type="'basic'"
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
                :type="'basic'"
              />
            </template>
          </DataList>
          <slot
            :name="`item-${index + 1}-append`"
            :item="item"
            :index="index"
            :type="'basic'"
          />
        </template>
      </div>

      <!-- Basic Footer 슬롯 -->
      <slot
        name="basic-footer"
        :data="data"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
// ============================================================================
// IMPORTS
// ============================================================================
import { BasicCard, DataList, ExpandableCard, TextButton, ToggleSwitch, Tooltip } from "@shc-nss/ui/solid";
import { computed, ref } from "vue";

// ============================================================================
// TYPES & INTERFACES
// ============================================================================

/** 개별 아이템 타입 */
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

/** 카드 스타일 설정 */
export interface CardStyleConfig {
  variant?: "outline" | "solid" | "elevated";
  color?: "default" | "gray" | "primary" | "secondary";
}

/** 데이터 구조 타입 */
export interface ScDataListData {
  /** 타이틀 */
  title?: string;
  /** 메인 아이템 목록 */
  items?: ScDataListItem[];
  /** Top 그룹 아이템 (boxInBox 전용) */
  topItems?: ScDataListItem[];
  /** Bottom 그룹 아이템 (boxInBox 전용) */
  bottomItems?: ScDataListItem[];
  /** 확장 아이템 목록 (expandable 전용) */
  expandedItems?: ScDataListItem[];
  /** 확장 버튼 라벨 (expandable 전용) */
  expandLabel?: string;
  /** 축소 버튼 라벨 (expandable 전용) */
  collapseLabel?: string;
  /** 카드 스타일 (box, boxInBox 전용) */
  cardStyle?: CardStyleConfig;
  /** 내부 카드 스타일 (boxInBox 전용) */
  innerCardStyle?: CardStyleConfig;
}

export interface ScDataListProps {
  /** 데이터 객체 */
  data?: ScDataListData;
  /** 타이틀 레벨 (heading level) */
  titleLevel?: number;
  /** 정렬 방식 */
  align?: "spaceBetween" | "left" | "center" | "right";
  /** 조밀한 스타일 */
  dense?: boolean;
  /** 구분선 표시 여부 */
  divider?: boolean;
  /** 클릭 가능 여부 */
  clickable?: boolean;
  /** 패딩 제거 */
  noPadding?: boolean;
  /** 접근성: 변경사항 음성 안내 */
  announceChanges?: boolean;
  /** 접근성: 커스텀 aria-label */
  ariaLabel?: string;
  /** 디버그 모드 (타입 정보 표시) */
  debug?: boolean;
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
  data: () => ({ items: [] }),
  titleLevel: 2,
  align: "spaceBetween",
  dense: false,
  divider: true,
  clickable: false,
  noPadding: false,
  announceChanges: true,
  debug: false,
});

// Emit 타입 정의
type SwitchChangePayload = {
  index: number;
  value: boolean;
  item: ScDataListItem;
  group: "top" | "main" | "bottom" | "default" | "expanded";
};

type ButtonClickPayload = {
  index: number;
  item: ScDataListItem;
  event: Event;
  group: "top" | "main" | "bottom" | "default" | "expanded";
};

type ItemClickPayload = {
  index: number;
  item: ScDataListItem;
  event: Event;
  group: "main" | "default";
};

type ContentClickPayload = {
  index: number;
  item: ScDataListItem;
  event: Event;
  group: "top" | "main" | "bottom" | "default" | "expanded";
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
const expanded = ref(false);

// ============================================================================
// COMPUTED - Type Detection (자동 타입 감지)
// ============================================================================

/**
 * 타입 감지 우선순위:
 * 1. Expandable (expandedItems 있음)
 * 2. BoxInBox (topItems 또는 bottomItems 있음)
 * 3. Box (cardStyle 있음, topItems/bottomItems 없음)
 * 4. Basic (나머지)
 */

/** Expandable 타입 여부 (expandedItems가 있으면) - 최우선 */
const isExpandable = computed(() => {
  return !!(props.data.expandedItems && props.data.expandedItems.length > 0);
});

/** BoxInBox 타입 여부 (topItems 또는 bottomItems가 있으면) - 2순위 */
const isBoxInBox = computed(() => {
  // Expandable이 아닐 때만 체크
  if (isExpandable.value) return false;
  
  return !!(
    (props.data.topItems && props.data.topItems.length > 0) ||
    (props.data.bottomItems && props.data.bottomItems.length > 0)
  );
});

/** Box 타입 여부 (cardStyle이 있으면) - 3순위 */
const isBox = computed(() => {
  // Expandable, BoxInBox가 아닐 때만 체크
  if (isExpandable.value || isBoxInBox.value) return false;
  
  return !!(props.data.cardStyle);
});

/** 카드가 필요한지 여부 */
const needsCard = computed(() => {
  return isBox.value || isBoxInBox.value;
});

/** Basic 타입 여부 (나머지 모든 경우) - 4순위 */
const isBasic = computed(() => {
  return !isExpandable.value && !needsCard.value;
});

/** 감지된 타입 (디버그용) */
const detectedType = computed(() => {
  if (isExpandable.value) return "expandable";
  if (isBoxInBox.value) return "boxInBox";
  if (isBox.value) return "box";
  return "basic";
});

/** 복합 유형 여부 (여러 타입 속성이 동시에 있는지) */
const hasMultipleTypes = computed(() => {
  let typeCount = 0;
  
  if (props.data.expandedItems?.length) typeCount++;
  if (props.data.topItems?.length || props.data.bottomItems?.length) typeCount++;
  if (props.data.cardStyle) typeCount++;
  
  return typeCount > 1;
});

// ============================================================================
// COMPUTED - Styles & Props
// ============================================================================

const containerClasses = computed(() => {
  return [
    "sc-data__list",
    `sc-data__list--${detectedType.value}`,
    props.dense && "sc-data__list--dense",
    props.clickable && "sc-data__list--clickable",
    props.noPadding && "sc-data__list--no-padding",
    hasMultipleTypes.value && "sc-data__list--composite",
  ].filter(Boolean);
});

const groupClasses = computed(() => {
  return [props.noPadding && "py-0"].filter(Boolean);
});

const computedAlign = computed(() => props.align);

/** 외부 카드 Props */
const cardProps = computed(() => {
  const style = props.data.cardStyle || {};
  
  if (isBox.value) {
    return {
      variant: style.variant || "solid",
      color: style.color || "gray",
    };
  }
  
  if (isBoxInBox.value) {
    return {
      variant: style.variant || "outline",
      color: style.color || "default",
    };
  }
  
  return {};
});

/** 내부 카드 Props (boxInBox 전용) */
const innerCardProps = computed(() => {
  const style = props.data.innerCardStyle || {};
  return {
    variant: style.variant || "solid",
    color: style.color || "gray",
  };
});

const accessibilityLabel = computed(() => {
  return props.ariaLabel || props.data.title || "데이터 목록";
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
  group: "top" | "main" | "bottom" | "default" | "expanded"
) => {
  let item: ScDataListItem | undefined;
  
  if (group === "top") {
    item = props.data.topItems?.[index];
  } else if (group === "main") {
    item = props.data.items?.[index];
  } else if (group === "bottom") {
    item = props.data.bottomItems?.[index];
  } else if (group === "default") {
    item = props.data.items?.[index];
  } else if (group === "expanded") {
    item = props.data.expandedItems?.[index];
  }

  if (!item) return;

  emit("switch-change", { index, value, item, group });

  if (props.announceChanges) {
    updateLiveRegion(`${item.title} ${value ? "활성화됨" : "비활성화됨"}`);
  }
};

const onButtonClick = (
  index: number,
  group: "top" | "main" | "bottom" | "default" | "expanded",
  event: Event
) => {
  let item: ScDataListItem | undefined;
  
  if (group === "top") {
    item = props.data.topItems?.[index];
  } else if (group === "main") {
    item = props.data.items?.[index];
  } else if (group === "bottom") {
    item = props.data.bottomItems?.[index];
  } else if (group === "default") {
    item = props.data.items?.[index];
  } else if (group === "expanded") {
    item = props.data.expandedItems?.[index];
  }

  if (!item) return;

  emit("button-click", { index, item, event, group });
};

const onItemClick = (index: number, event: Event, group: "main" | "default") => {
  if (!props.clickable) return;
  const item = props.data.items?.[index];
  if (!item) return;
  emit("item-click", { index, item, event, group });
};

const onContentClick = (
  index: number,
  group: "top" | "main" | "bottom" | "default" | "expanded",
  event: Event
) => {
  let item: ScDataListItem | undefined;
  
  if (group === "top") {
    item = props.data.topItems?.[index];
  } else if (group === "main") {
    item = props.data.items?.[index];
  } else if (group === "bottom") {
    item = props.data.bottomItems?.[index];
  } else if (group === "expanded") {
    item = props.data.expandedItems?.[index];
  } else if (group === "default") {
    item = props.data.items?.[index];
  }

  if (!item) return;

  emit("content-click", { index, item, event, group });
};

const onItemKeydown = (event: KeyboardEvent, index: number) => {
  const { key } = event;

  if (key === "Enter" || key === " ") {
    event.preventDefault();
    onItemClick(index, event, "main");
  } else if (key === "ArrowDown") {
    event.preventDefault();
    focusNextItem(index);
  } else if (key === "ArrowUp") {
    event.preventDefault();
    focusPrevItem(index);
  }
};

const focusNextItem = (currentIndex: number) => {
  const itemCount = props.data.items?.length || 0;
  const nextIndex = Math.min(currentIndex + 1, itemCount - 1);
  focusedIndex.value = nextIndex;
};

const focusPrevItem = (currentIndex: number) => {
  const prevIndex = Math.max(currentIndex - 1, 0);
  focusedIndex.value = prevIndex;
};

const toggleExpanded = () => {
  if (isExpandable.value) {
    expanded.value = !expanded.value;
  }
};

// ============================================================================
// EXPOSE
// ============================================================================
defineExpose({
  /** 타입 정보 */
  getType: () => detectedType.value,
  /** 복합 유형 여부 */
  isComposite: () => hasMultipleTypes.value,
  /** 타입별 플래그 */
  isExpandable: () => isExpandable.value,
  isBoxInBox: () => isBoxInBox.value,
  isBox: () => isBox.value,
  isBasic: () => isBasic.value,
  /** 데이터 가져오기 */
  getData: () => props.data,
  getItems: () => props.data.items,
  getItemByIndex: (index: number) => props.data.items?.[index],
  getTopItems: () => props.data.topItems,
  getBottomItems: () => props.data.bottomItems,
  getExpandedItems: () => props.data.expandedItems,
  /** 확장 상태 제어 (expandable 전용) */
  toggleExpanded,
  getExpanded: () => expanded.value,
  setExpanded: (value: boolean) => { expanded.value = value; },
  /** 특정 아이템에 포커스 */
  focusItem: (index: number) => {
    focusedIndex.value = index;
  },
  /** Live Region 업데이트 (접근성) */
  announce: (message: string) => updateLiveRegion(message),
});
</script>

<style scoped lang="scss">
// ============================================================================
// Screen Reader Only
// ============================================================================
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

// ============================================================================
// Container Styles
// ============================================================================
.sc-data__list {
  width: 100%;

  &--dense {
    :deep(.data-list__group) {
      gap: 8px;
    }
  }

  &--clickable {
    :deep(.sv-data-list) {
      cursor: pointer;
      transition: background-color 0.2s ease;

      &:hover {
        background-color: rgba(0, 0, 0, 0.02);
      }

      &:focus {
        outline: 2px solid #007aff;
        outline-offset: 2px;
      }
    }
  }

  &--no-padding {
    :deep(.data-list__group) {
      padding: 0;
    }
  }

  // 복합 유형 스타일
  &--composite {
    border: 2px dashed rgba(255, 165, 0, 0.3);
    padding: 8px;
    border-radius: 4px;
  }
}

// ============================================================================
// Debug Styles
// ============================================================================
.sc-data__debug {
  background: #f0f0f0;
  border: 1px solid #ccc;
  padding: 8px 12px;
  margin-bottom: 12px;
  border-radius: 4px;
  font-size: 12px;
  font-family: monospace;
  color: #333;

  strong {
    color: #007aff;
  }

  span {
    color: #ff6600;
    font-weight: bold;
  }
}

// ============================================================================
// Expandable Styles
// ============================================================================
.expandable-data-list {
  width: 100%;
}

// ============================================================================
// Content Styles
// ============================================================================
.text-clickable {
  cursor: pointer;
  transition: color 0.2s ease;

  &:hover {
    color: #007aff;
    text-decoration: underline;
  }
}

// ============================================================================
// Accessibility
// ============================================================================
@media (prefers-reduced-motion: reduce) {
  .sc-data__list * {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}

@media (prefers-contrast: high) {
  .sc-data__list {
    outline: 1px solid;
  }
}
</style>

