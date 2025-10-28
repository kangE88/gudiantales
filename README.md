<route lang="yaml">
meta:
  title: TabScrollmove
  description: SHC UI 테이블 컴포넌트입니다.
  author: dkang
  category: Data
</route>
<template>
  <h1 class="sr-only">상단 네비게이션 타이틀 또는 본문 타이틀</h1>

  <!-- 콘텐츠 영역 -->
  <div class="sv-contents__body">
    <div class="c-tabs__group is-sticky">
      <Tabs
        v-model="activeTab"
        @update:model-value="handleTabChange"
        :items="[{ label: '텍스트' }, { label: '텍스트' }]"
      />

      <Tabs v-model="TabsLineActive">
        <Tab
          v-for="(t, index) in TabsLine"
          :key="index"
          :label="t.label"
        >
          {{ t.label }}
        </Tab>
      </Tabs>

      <Tabs
        v-model="TabsSecondaryActive"
        type="secondary"
      >
        <Tab
          v-for="(p, index) in TabsSecondary"
          :key="index"
          :label="p.label"
          :iconName="p.iconName"
          :disabled="p.disabled"
          :dot="p.dot"
        >
          {{ p.label }}
        </Tab>
      </Tabs>
    </div>

    <!-- 콘텐츠 빈 영역 표시(디자인 스타일) -->
    <section class="section">
      <div
        ref="contentRef"
        class="c-empty__area swipeable-content"
        style="height: 1000px"
      >
        <div class="content-display">
          <h2>Line Tabs 현재 활성: {{ TabsLineActive + 1 }} / {{ TabsLine.length }}</h2>
          <p class="swipe-hint">👈 좌우로 스와이프하여 탭을 이동할 수 있습니다 👉</p>
          <div class="tab-info">
            <p><strong>스와이프 제어 대상:</strong> Line Tabs (두 번째 탭 그룹)</p>
            <p>현재 항목: {{ TabsLine[TabsLineActive]?.label }}</p>
            <hr style="margin: 12px 0; border: none; border-top: 1px solid #dee2e6" />
            <p>첫 번째 Tabs (activeTab): {{ activeTab }}</p>
            <p>Line Tabs (TabsLineActive): {{ TabsLineActive }}</p>
            <p>Secondary Tabs (TabsSecondaryActive): {{ TabsSecondaryActive }}</p>
          </div>
        </div>
      </div>
    </section>
  </div>

  <!-- <BottomActionContainer :scrollDim="true">
      <BoxButtonGroup size="xlarge" variant="100">
        <BoxButton text="텍스트" />
      </BoxButtonGroup>
    </BottomActionContainer> -->
</template>

<script setup lang="ts">
import { Tab, Tabs } from "@/components/Tabs";
import { usePointerSwipe } from "@vueuse/core";
import { ref } from "vue";

// 첫 번째 Tabs
const activeTab = ref(0);
const handleTabChange = (newValue: number | string) => {
  console.log("Tab changed to:", newValue);
  activeTab.value = typeof newValue === "number" ? newValue : parseInt(String(newValue), 10);
};

// Line Tabs (두 번째 Tabs)
const TabsLineActive = ref(0);
const TabsLine = [
  { label: "항목1" },
  { label: "항목2" },
  { label: "항목3" },
  { label: "항목4" },
  { label: "항목5" },
  { label: "항목6" },
  { label: "항목7" },
  { label: "항목8" },
  { label: "항목9" },
  { label: "항목10" },
];

// Secondary Tabs (세 번째 Tabs)
const TabsSecondaryActive = ref(0);
const TabsSecondary = [
  { label: "항목1" },
  { label: "항목2" },
  { label: "항목3" },
  { label: "항목4" },
  { label: "항목5" },
  { label: "항목6" },
  { label: "항목7" },
  { label: "항목8", iconName: "sample-icon" },
  { label: "항목9", dot: true },
  { label: "항목10", disabled: true },
];

// 콘텐츠 영역에 스와이프 기능 추가 (마우스 + 터치 지원)
const contentRef = ref<HTMLElement>();

// usePointerSwipe는 마우스 드래그와 터치 스와이프를 모두 지원합니다
usePointerSwipe(contentRef, {
  threshold: 50, // 최소 50px 이동해야 스와이프로 인식
  onSwipeEnd(_e: PointerEvent, direction: "left" | "right" | "up" | "down" | "none") {
    console.log("Swipe detected:", direction);

    if (direction === "left") {
      // 왼쪽으로 스와이프 -> 다음 탭으로 이동
      navigateToNextTab();
    } else if (direction === "right") {
      // 오른쪽으로 스와이프 -> 이전 탭으로 이동
      navigateToPrevTab();
    }
  },
  onSwipe(_e: PointerEvent) {
    // 스와이프 중 시각적 피드백 (필요시 활용)
  },
});

const navigateToNextTab = () => {
  // Line Tabs (10개 항목)의 다음 탭으로 이동
  if (TabsLineActive.value < TabsLine.length - 1) {
    TabsLineActive.value += 1;
    console.log("Next tab:", TabsLineActive.value);
  }
};

const navigateToPrevTab = () => {
  // Line Tabs (10개 항목)의 이전 탭으로 이동
  if (TabsLineActive.value > 0) {
    TabsLineActive.value -= 1;
    console.log("Previous tab:", TabsLineActive.value);
  }
};
</script>

<style lang="scss" scoped>
.swipeable-content {
  cursor: grab;
  user-select: none;
  touch-action: pan-y; // 세로 스크롤은 허용하면서 좌우 스와이프 감지
  display: flex;
  align-items: center;
  justify-content: center;

  &:active {
    cursor: grabbing;
  }

  .content-display {
    text-align: center;
    padding: 40px;

    h2 {
      font-size: 24px;
      font-weight: 700;
      margin-bottom: 20px;
      color: #212529;
    }

    .swipe-hint {
      display: inline-block;
      padding: 16px 24px;
      background-color: #e7f3ff;
      border-radius: 8px;
      font-size: 16px;
      color: #0066cc;
      margin-bottom: 24px;
      font-weight: 500;
    }

    .tab-info {
      margin-top: 32px;
      padding: 24px;
      background-color: #f8f9fa;
      border-radius: 8px;
      text-align: left;

      p {
        font-size: 14px;
        line-height: 1.8;
        color: #495057;
        margin-bottom: 8px;

        &:last-child {
          margin-bottom: 0;
        }
      }
    }
  }
}
</style>
