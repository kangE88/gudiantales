import { usePointerSwipe, type UseSwipeDirection } from "@vueuse/core";
import { computed, readonly, ref, watch, watchEffect, type Ref } from "vue";

// 상수 정의
const RESISTANCE_FACTOR = 0.1; // 끝 경계에서의 저항 계수
const TRANSITION_DURATION = 350; // ms
const TRANSITION_EASING = 'cubic-bezier(0.4, 0, 0.2, 1)';

/**
 * @name useTabSwipe
 * @description 탭 패널에 스와이프 기능을 추가하는 composable
 * @param {Ref<HTMLElement | undefined>} targetRef 스와이프를 적용 할 Panel의 ref
 * @param {Ref<number>} activeTabRef 현재 활성 탭 인덱스 ref
 * @param {number} tabCount 전체 탭 갯수
 * @param {number} threshold 스와이프 감지하는 수치 (default: 50px)
 * @returns {object} 탭 네비게이션 함수 및 상태
 */
export function useTabSwipe(
  targetRef: Ref<HTMLElement | undefined>,
  activeTabRef: Ref<number>,
  tabCount: number,
  threshold: number = 50
) {
  const swipeOffset = ref(0);
  const isSwiping = ref(false);
  const startX = ref(0);

  const navigateToNextTab = (): void => {
    if (activeTabRef.value < tabCount - 1) {
      activeTabRef.value += 1;
    }
  };

  const navigateToPrevTab = (): void => {
    if (activeTabRef.value > 0) {
      activeTabRef.value -= 1;
    }
  };

  const swipeTargetRef = computed<HTMLElement | undefined>(() => {
    const element = targetRef.value;
    if (!element) return undefined;

    const el = (element as any).$el || element;
    const panelsElement = el.querySelector?.(".sv-tabs__panels");
    
    return panelsElement ? (panelsElement as HTMLElement) : el;
  });

  // 패널 초기화 (한 번만 실행)
  const initializePanels = () => {
    const container = swipeTargetRef.value;
    if (!container) return;

    const panels = container.querySelectorAll('.sv-tabs__panel');
    if (panels.length === 0) return;

    // 컨테이너 스타일 설정
    Object.assign(container.style, {
      position: 'relative',
      overflow: 'hidden',
      touchAction: 'pan-y'
    });

    // 각 패널 초기 설정 (한 번만)
    panels.forEach((panel: Element, index: number) => {
      const htmlPanel = panel as HTMLElement;
      
      // 기본 스타일 일괄 적용
      Object.assign(htmlPanel.style, {
        display: 'block',
        position: 'absolute',
        top: '0',
        left: '0',
        width: '100%',
        willChange: 'transform' // GPU 가속 최적화
      });
      
      // 데이터 속성으로 인덱스 저장
      htmlPanel.dataset.panelIndex = String(index);
    });
  };

  // transform만 업데이트 (최소한의 DOM 조작)
  const updateTransforms = (offset: number, transitioning: boolean) => {
    const container = swipeTargetRef.value;
    if (!container) return;

    const panels = container.querySelectorAll('.sv-tabs__panel');
    if (panels.length === 0) return;

    const containerWidth = container.offsetWidth || 1;
    const offsetPercent = (offset / containerWidth) * 100;
    
    panels.forEach((panel: Element) => {
      const htmlPanel = panel as HTMLElement;
      const index = Number(htmlPanel.dataset.panelIndex || 0);
      
      const baseTransform = (index - activeTabRef.value) * 100;
      const finalTransform = baseTransform + offsetPercent;
      
      // transition만 토글
      htmlPanel.style.transition = transitioning 
        ? `transform ${TRANSITION_DURATION}ms ${TRANSITION_EASING}`
        : 'none';
      
      htmlPanel.style.transform = `translateX(${finalTransform}%)`;
    });
  };

  usePointerSwipe(swipeTargetRef, {
    threshold,
    onSwipeStart(e: PointerEvent) {
      isSwiping.value = true;
      startX.value = e.clientX;
      swipeOffset.value = 0;
    },
    onSwipe(e: PointerEvent) {
      if (!isSwiping.value) return;
      
      const distanceX = e.clientX - startX.value;
      
      // 첫 탭에서 오른쪽으로, 마지막 탭에서 왼쪽으로 스와이프 시 저항 적용
      const isFirstTab = activeTabRef.value === 0;
      const isLastTab = activeTabRef.value === tabCount - 1;
      
      if ((isFirstTab && distanceX > 0) || (isLastTab && distanceX < 0)) {
        swipeOffset.value = distanceX * RESISTANCE_FACTOR;
      } else {
        swipeOffset.value = distanceX;
      }
      
      // 스와이프 중 실시간 업데이트 (transition 없음)
      updateTransforms(swipeOffset.value, false);
    },
    onSwipeEnd(_e: PointerEvent, direction: UseSwipeDirection) {
      isSwiping.value = false;
      swipeOffset.value = 0;

      if (direction === "left" && activeTabRef.value < tabCount - 1) {
        navigateToNextTab();
      } else if (direction === "right" && activeTabRef.value > 0) {
        navigateToPrevTab();
      } else {
        // 스와이프가 threshold에 도달하지 못한 경우 원위치로 복귀
        updateTransforms(0, true);
      }
    },
  });

  // 초기화 (한 번만 실행)
  watchEffect(() => {
    const el = swipeTargetRef.value;
    if (el?.querySelector('.sv-tabs__panel')) {
      initializePanels();
      updateTransforms(0, false);
    }
  });

  // activeTabRef가 변경될 때 transition과 함께 이동
  watch(activeTabRef, () => {
    if (!isSwiping.value) {
      updateTransforms(0, true);
    }
  });

  return { 
    navigateToNextTab, 
    navigateToPrevTab,
    swipeOffset: readonly(swipeOffset),
    isSwiping: readonly(isSwiping)
  };
}
