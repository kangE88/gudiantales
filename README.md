import { usePointerSwipe, type UseSwipeDirection } from "@vueuse/core";
import { computed, ref, watch, watchEffect, type Ref } from "vue";

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
    
    if (panelsElement) {
      console.log("✅ Found .sv-tabs__panels for swipe");
      return panelsElement as HTMLElement;
    }
    console.log("⚠️ Using original element for swipe");
    return el;
  });

  // 패널 컨테이너의 스타일 업데이트
  const updatePanelsStyle = (offset: number, transitioning: boolean) => {
    const panelsContainer = swipeTargetRef.value;
    if (!panelsContainer) {
      console.warn("⚠️ No panels container found");
      return;
    }

    const panels = panelsContainer.querySelectorAll('.sv-tabs__panel');
    
    if (panels.length === 0) {
      console.warn("⚠️ No .sv-tabs__panel elements found");
      return;
    }

    console.log(`📱 Updating ${panels.length} panels, offset: ${offset}, transitioning: ${transitioning}`);
    
    panels.forEach((panel: Element, index: number) => {
      const htmlPanel = panel as HTMLElement;
      
      // 모든 패널을 보이게 설정 (display: none 오버라이드)
      htmlPanel.style.display = 'block';
      htmlPanel.style.position = 'absolute';
      htmlPanel.style.top = '0';
      htmlPanel.style.left = '0';
      htmlPanel.style.width = '100%';
      
      // transform 계산
      const baseTransform = (index - activeTabRef.value) * 100;
      const offsetPercent = (offset / panelsContainer.offsetWidth) * 100;
      
      if (transitioning) {
        htmlPanel.style.transition = 'transform 0.35s cubic-bezier(0.4, 0, 0.2, 1)';
      } else {
        htmlPanel.style.transition = 'none';
      }
      
      htmlPanel.style.transform = `translateX(${baseTransform + offsetPercent}%)`;
    });
    
    // 컨테이너는 relative positioning
    panelsContainer.style.position = 'relative';
  };

  usePointerSwipe(swipeTargetRef, {
    threshold,
    onSwipeStart(_e: PointerEvent) {
      isSwiping.value = true;
      startX.value = _e.clientX;
      swipeOffset.value = 0;
    },
    onSwipe(_e: PointerEvent) {
      if (!isSwiping.value) return;
      
      const distanceX = _e.clientX - startX.value;
      
      // 첫 탭에서 오른쪽으로, 마지막 탭에서 왼쪽으로 스와이프 시 저항 적용
      const isFirstTab = activeTabRef.value === 0;
      const isLastTab = activeTabRef.value === tabCount - 1;
      
      if ((isFirstTab && distanceX > 0) || (isLastTab && distanceX < 0)) {
        // 끝에서의 저항 효과 (30% 감소)
        swipeOffset.value = distanceX * 0.3;
      } else {
        swipeOffset.value = distanceX;
      }
      
      // 스와이프 중 실시간 업데이트 (transition 없음)
      updatePanelsStyle(swipeOffset.value, false);
    },
    onSwipeEnd(_e: PointerEvent, direction: UseSwipeDirection) {
      isSwiping.value = false;
      const finalOffset = swipeOffset.value;
      swipeOffset.value = 0;

      if (direction === "left" && activeTabRef.value < tabCount - 1) {
        navigateToNextTab();
      } else if (direction === "right" && activeTabRef.value > 0) {
        navigateToPrevTab();
      } else {
        // 스와이프가 threshold에 도달하지 못한 경우 원위치로 복귀
        updatePanelsStyle(0, true);
      }
    },
  });

  // touch-action을 pan-y로 설정하여 세로 스크롤은 허용하고 가로 스와이프만 감지
  watchEffect(() => {
    const el = swipeTargetRef.value;
    if (el) {
      el.style.touchAction = "pan-y";
      console.log("✅ Set touch-action: pan-y");
    }
  });

  // 초기 스타일 설정 및 패널 구조 확인
  watchEffect(() => {
    const el = swipeTargetRef.value;
    if (el) {
      const panels = el.querySelectorAll('.sv-tabs__panel');
      if (panels.length > 0) {
        console.log(`🎨 Initial setup for ${panels.length} panels`);
        updatePanelsStyle(0, false);
      }
    }
  });

  // activeTabRef가 변경될 때 transition과 함께 이동
  watch(activeTabRef, () => {
    if (!isSwiping.value) {
      console.log(`🔄 Tab changed to ${activeTabRef.value}`);
      updatePanelsStyle(0, true);
    }
  });

  return { 
    navigateToNextTab, 
    navigateToPrevTab,
    swipeOffset,
    isSwiping 
  };
}
