import { ref, computed, onMounted, onUnmounted, watch } from “vue”;
import { useThrottleFn, useWindowScroll, useWindowSize } from “@vueuse/core”;

export interface UseBottomActionLayoutOptions {
/**

- 바텀 액션 컨테이너 셀렉터
- @default ‘.sv-bottom-action-container’
  */
  bottomSelector?: string;

/**

- 메인 컨테이너 셀렉터
- @default ‘.sc-container’
  */
  containerSelector?: string;

/**

- 스크롤 감지 스로틀 시간 (ms)
- @default 10
  */
  scrollThrottle?: number;

/**

- 하단 스크롤 임계값 (px)
- @default 10
  */
  scrollThreshold?: number;

/**

- CSS 변수명
- @default ‘–sc-bottom-action-height’
  */
  cssVarName?: string;

/**

- 루트 엘리먼트 (기본값: document)
  */
  rootElement?: HTMLElement | Document;
  }

/**

- 바텀 액션 레이아웃을 관리하는 컴포저블
- 
- @example
- ```vue
  
  ```
- <script setup>
- import { useBottomActionLayout } from ‘@/composables/useBottomActionLayout’
- 
- const {
- bottomHeight,
- bottomHeightRem,
- isScrolled,
- isAtBottom
- } = useBottomActionLayout()
- </script>
- 
- <template>
- <div class="sc-container">
- ```
  <main class="main-content">
  ```
- ```
    <!-- 메인 컨텐츠 -->
  ```
- ```
    <p>바텀 높이: {{ bottomHeight }}px ({{ bottomHeightRem }}rem)</p>
  ```
- ```
  </main>
  ```
- 
- ```
  <div 
  ```
- ```
    class="sv-bottom-action-container"
  ```
- ```
    :class="{ 'is-scrolled': isScrolled }"
  ```
- ```
  >
  ```
- ```
    <!-- 바텀 액션 버튼들 -->
  ```
- ```
    <button>저장</button>
  ```
- ```
    <button>취소</button>
  ```
- ```
  </div>
  ```
- </div>
- </template>
- 
- <style>
- .sc-container {
- position: relative;
- padding-bottom: var(–sc-bottom-action-height, 0);
- }
- 
- .main-content {
- min-height: 100vh;
- }
- 
- .sv-bottom-action-container {
- position: fixed;
- bottom: 0;
- left: 0;
- right: 0;
- background: white;
- padding: 1rem;
- box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
- transition: box-shadow 0.3s;
- }
- 
- .sv-bottom-action-container.is-scrolled {
- box-shadow: 0 -4px 16px rgba(0, 0, 0, 0.15);
- }
- </style>
- ```
  
  ```

*/
export function useBottomActionLayout(options: UseBottomActionLayoutOptions = {}) {
const {
bottomSelector = “.sv-bottom-action-container”,
containerSelector = “.sc-container”,
scrollThrottle = 10,
scrollThreshold = 10,
cssVarName = “–sc-bottom-action-height”,
rootElement = document,
} = options;

// 반응형 상태
const bottomHeight = ref(0);
const bottomHeightRem = ref(0);
const isScrolled = ref(false);
const isAtBottom = ref(false);

// 옵저버 인스턴스
let resizeObserver: ResizeObserver | null = null;
let mutationObserver: MutationObserver | null = null;

// VueUse hooks
const { y: scrollY } = useWindowScroll();
const { height: windowHeight } = useWindowSize();

/**

- 스크롤 위치 확인 (스로틀 적용)
  */
  const checkScrollPosition = useThrottleFn(() => {
  const documentHeight = document.documentElement.scrollHeight;
  const atBottom = scrollY.value + windowHeight.value >= documentHeight - scrollThreshold;

```
isAtBottom.value = atBottom;
isScrolled.value = !atBottom;
```

}, scrollThrottle);

/**

- 바텀 높이 계산 및 CSS 변수 적용
  */
  const applyBottomHeight = () => {
  const scopeEl = rootElement instanceof HTMLElement ? rootElement : document;

```
// 컨테이너 찾기
const container =
  rootElement instanceof HTMLElement && rootElement.classList.contains(containerSelector.slice(1))
    ? rootElement
    : (scopeEl.querySelector(containerSelector) as HTMLElement | null);

// 바텀 액션 컨테이너 찾기
const bottomActionEl = scopeEl.querySelector(bottomSelector) as HTMLElement | null;

if (!container || !bottomActionEl) {
  bottomHeight.value = 0;
  bottomHeightRem.value = 0;
  if (container) {
    container.style.removeProperty(cssVarName);
  }
  return;
}

// 높이 계산
const rect = bottomActionEl.getBoundingClientRect();
const heightPx = Math.ceil(rect.height);

// px -> rem 변환
const rootFontSize = parseFloat(getComputedStyle(document.documentElement).fontSize) || 16;
const heightRem = heightPx / rootFontSize;

// 상태 업데이트
bottomHeight.value = heightPx;
bottomHeightRem.value = heightRem;

// CSS 변수 설정
if (heightPx > 0) {
  container.style.setProperty(cssVarName, `${heightRem}rem`);
} else {
  container.style.removeProperty(cssVarName);
}
```

};

/**

- 옵저버 설정
  */
  const setupObservers = () => {
  try {
  const scopeEl = rootElement instanceof HTMLElement ? rootElement : document;
  
  const container =
  rootElement instanceof HTMLElement && rootElement.classList.contains(containerSelector.slice(1))
  ? rootElement
  : (scopeEl.querySelector(containerSelector) as HTMLElement | null);
  
  const bottomActionEl = scopeEl.querySelector(bottomSelector) as HTMLElement | null;
  
  if (!container) return;
  
  // ResizeObserver 설정 (높이 변화 감지)
  if (“ResizeObserver” in window) {
  resizeObserver = new ResizeObserver(() => {
  applyBottomHeight();
  });
  
  if (bottomActionEl) {
  resizeObserver.observe(bottomActionEl);
  }
  }
  
  // MutationObserver 설정 (DOM 변화 감지)
  mutationObserver = new MutationObserver(() => {
  applyBottomHeight();
  
  // 바텀 액션 엘리먼트가 변경되면 ResizeObserver 재설정
  const currentBottomEl = scopeEl.querySelector(bottomSelector) as HTMLElement | null;
  
  if (resizeObserver) {
  resizeObserver.disconnect();
  if (currentBottomEl) {
  resizeObserver.observe(currentBottomEl);
  }
  }
  });
  
  mutationObserver.observe(container, {
  childList: true,
  subtree: true,
  });
  } catch (error) {
  console.error(“Failed to setup observers:”, error);
  }
  };

/**

- 옵저버 정리
  */
  const cleanupObservers = () => {
  if (resizeObserver) {
  resizeObserver.disconnect();
  resizeObserver = null;
  }
  if (mutationObserver) {
  mutationObserver.disconnect();
  mutationObserver = null;
  }
  };

// 스크롤 감지
watch([scrollY, windowHeight], () => {
checkScrollPosition();
});

// 초기화
onMounted(() => {
applyBottomHeight();
setupObservers();
checkScrollPosition();
});

// 정리
onUnmounted(() => {
cleanupObservers();
});

return {
// 상태
bottomHeight: computed(() => bottomHeight.value),
bottomHeightRem: computed(() => bottomHeightRem.value),
isScrolled: computed(() => isScrolled.value),
isAtBottom: computed(() => isAtBottom.value),

```
// 메서드
refresh: applyBottomHeight,
cleanup: cleanupObservers,
```

};
}