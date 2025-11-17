import { usePointerSwipe, type UseSwipeDirection } from "@vueuse/core";
import { computed, watchEffect, type Ref } from "vue";

/**
 * @name useTabSwipe
 * @param {Ref<HTMLElement | undefined>} targetRef 스와이프를 적용 할 Panel의 ref
 * @param {Ref<number>} activeTabRef 현재 활성 탭 인덱스 ref
 * @param {number} tabCount 전체 탭 갯수
 * @param {number} threshold 스와이프 감지하는 수치 default: 50px) 왼쪽,오른쪽 50px 움직여야 스와이프 동작
 * @returns {object}
 */
export function useTabSwipe(
  targetRef: Ref<HTMLElement | undefined>,
  activeTabRef: Ref<number>,
  tabCount: number,
  threshold: number = 50
) {
  const navigateToNextTab = (): void => {
    if (activeTabRef.value < tabCount - 1) {
      activeTabRef.value += 1;
      // console.log(activeTabRef.value);
    }
  };
  const navigateToPrevTab = (): void => {
    if (activeTabRef.value > 0) {
      activeTabRef.value -= 1;
      // console.log(activeTabRef.value);
    }
  };

  const swipeTargetRef = computed<HTMLElement | undefined>(() => {
    const element = targetRef.value;
    if (!element) return undefined;

    const el = (element as any).$el || element;

    const panelsElement = el.querySelector?.(".sv-tabs__panels");
    if (panelsElement) {
      // console.log("Found panels for swipe");
      return panelsElement as HTMLElement;
    }
    return el;
  });
  usePointerSwipe(swipeTargetRef, {
    threshold,
    onSwipeEnd(_e: PointerEvent, direction: UseSwipeDirection) {
      if (direction === "left") {
        navigateToNextTab();
      } else if (direction === "right") {
        navigateToPrevTab();
      }
    },
    onSwipe(_e: PointerEvent) {},
  });

  watchEffect(() => {
    const el = swipeTargetRef.value;
    if (el) {
      el.style.touchAction = "pan-y";
    }
  });

  return { navigateToNextTab, navigateToPrevTab };
}


<route lang="yaml">
meta:
  id: SBT011A01
  title: 반갑꾸러미
  menu: "혜택: 투데이 Tab > 멤버십·리워드​ > 반갑 꾸러미"
  layout: SubLayout
  category: 혜택
  publish: 김대민
  publishVersion: 0.8
  status: 재작업
  etc:
    "tabpanel 내용 호출해서
    사용\n매일결제-SBT011A01-daily\n처음결제-SBT011A01-first\n매달결제-SBT011A01-monthly\n251105:
    ScIcon > ScImageIcon 으로 변경\n251105: 이미지용량 문제로 svg 아이콘 .png로 변경 후 경로수정"
  header:
    variant: sub
    fixed: true
    back: true
    close: true
</route>
<template>
  <div class="sc-contents__body welcome-lounge">
    <Tabs
      v-model="selectedTab"
      ref="contentRef"
      panels-class="welcome-panels"
      :panels-style="panelsInlineStyle"
    >
      <!-- <Tab>매일결제</Tab>
        <Tab>처음결제</Tab>
        <Tab>매달결제</Tab> -->
      <Tab
        v-for="(t, index) in TabsLine"
        :key="index"
        :label="t.label"
      >
        {{ t.label }}
      </Tab>

      <TabPanel
        v-for="(panel, index) in panelComponents"
        :key="panel.key"
        :style="getPanelStyle(index)"
        :aria-hidden="selectedTab !== index"
      >
        <component :is="panel.component" />
      </TabPanel>
    </Tabs>
  </div>
</template>

<script setup>
import { useTabSwipe } from "@shc-nss/shared/utils";
import { Tab, TabPanel, Tabs } from "@shc-nss/ui/solid";
import { computed, ref } from "vue";
import SBT011A01Daily from "./section/SBT011A01-daily.vue";
import SBT011A01First from "./section/SBT011A01-first.vue";
import SBT011A01Monthly from "./section/SBT011A01-monthly.vue";

const selectedTab = ref(0);

const TabsLine = [{ label: "매일결제" }, { label: "처음결제" }, { label: "매달결제" }];
const panelComponents = [
  { key: "daily", component: SBT011A01Daily },
  { key: "first", component: SBT011A01First },
  { key: "monthly", component: SBT011A01Monthly },
];
// 콘텐츠 영역에 스와이프 기능 추가 (마우스 + 터치 지원)
// const contentRef = ref<HTMLElement>();
const contentRef = ref(null);

const { navigateToNextTab, navigateToPrevTab } = useTabSwipe(
  contentRef,
  selectedTab,
  TabsLine.length,
  50
);

const panelsInlineStyle = computed(() => ({
  "--active-tab-index": `${selectedTab.value}`,
}));

const getPanelStyle = (index) => ({
  "--panel-index": `${index}`,
});
</script>

<style scoped lang="scss">
:deep(.welcome-panels) {
  position: relative;
  overflow: hidden;
}

:deep(.welcome-panels .sv-tabs__panel) {
  display: block !important;
  width: 100%;
  position: absolute;
  top: 0;
  left: 0;
  transition: transform 0.35s ease;
  transform: translateX(calc((var(--panel-index, 0) - var(--active-tab-index, 0)) * 100%));
  pointer-events: none;
}

:deep(.welcome-panels .sv-tabs__panel--active) {
  position: relative;
  pointer-events: auto;
}
</style>
