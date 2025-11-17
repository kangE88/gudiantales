import type { UseSortableOptions } from "@vueuse/integrations/useSortable";
import { useSortable } from "@vueuse/integrations/useSortable";
import type { Ref } from "vue";

/**
 * ========================================
 * Sortable.js 주요 옵션 가이드
 * ========================================
 *
 * 1. 기본 옵션
 *    - animation: number - 애니메이션 속도 (ms 단위)
 *    - scrollSensitivity: number - 스크롤 시작 감도 (px 단위, 가장자리에서 얼마나 가까워야 스크롤 시작)
 *    - scrollSpeed: number - 자동 스크롤 속도
 *
 * 2. Handle 옵션
 *    - handle: string - 드래그 핸들 CSS 선택자 (예: '.drag-handle')
 *                       이 요소를 잡아야만 드래그 가능
 *
 * 3. Delay 옵션
 *    - delay: number - 드래그 시작 지연 시간 (ms 단위)
 *                      실수로 인한 드래그 방지, 모바일 환경에 유용
 *
 * 4. Disabled 옵션
 *    - disabled: boolean - 드래그 기능 활성화/비활성화
 *                          동적으로 제어 가능
 *
 * 5. Filter 옵션
 *    - filter: string - 드래그를 방지할 항목의 CSS 선택자 (예: '.locked-item')
 *                       해당 클래스를 가진 항목은 드래그 불가
 *
 * 6. Draggable 옵션
 *    - draggable: string - 드래그 가능한 항목의 CSS 선택자 (예: '.draggable-item')
 *                          이 선택자에 해당하는 항목만 드래그 가능
 *
 * 7. Group 옵션
 *    - group: string | { name, pull, put } - 여러 리스트 간 항목 이동 설정
 *      * string: 그룹 이름 (같은 이름끼리 항목 이동 가능)
 *      * object:
 *        - name: string - 그룹 이름
 *        - pull: boolean | 'clone' - 항목을 다른 리스트로 이동 가능 여부 (clone: 복사본 생성)
 *        - put: boolean | string[] - 다른 리스트에서 항목 받을 수 있는지 여부
 *
 * 8. Sort 옵션
 *    - sort: boolean - 리스트 내부 정렬 허용 여부
 *                      false 설정 시 같은 리스트 내에서는 정렬 불가, 다른 그룹으로만 이동 가능
 *
 * 9. Direction 옵션
 *    - direction: 'vertical' | 'horizontal' - 정렬 방향
 *                 기본값은 'vertical' (세로)
 *
 * 10. SwapThreshold & InvertSwap 옵션
 *     - swapThreshold: number - 항목 교환 감도 (0~1, 기본값 1)
 *                               낮을수록 교환이 더 쉽게 일어남
 *     - invertSwap: boolean - 스왑 방향 반전으로 부드러운 교환 효과
 *
 * 이벤트 핸들러:
 *    - onStart: (evt) => void - 드래그 시작 시
 *    - onEnd: (evt) => void - 드래그 종료 시
 *    - onAdd: (evt) => void - 다른 리스트에서 항목 추가 시
 *    - onUpdate: (evt) => void - 리스트 내 항목 순서 변경 시
 *    - onRemove: (evt) => void - 다른 리스트로 항목 이동 시
 *    - onFilter: (evt) => void - 필터링된 항목 클릭 시
 *    - onSort: (evt) => void - 정렬 발생 시
 *    - onMove: (evt) => boolean - 드래그 중 이동 시마다 호출 (false 반환 시 이동 취소)
 *
 * 기타 옵션:
 *    - swap: boolean - 스왑 모드 활성화 (플러그인 필요)
 *    - multiDrag: boolean - 다중 선택 드래그 (플러그인 필요)
 * ========================================
 */

/**
 * 드래그 앤 드롭으로 리스트 정렬을 가능하게 하는 공통 함수
 *
 * @description
 * - Sortable.js를 기반으로 한 VueUse의 useSortable을 래핑
 * - 기본 설정이 적용되어 간편하게 사용 가능
 * - animation, scrollSensitivity, scrollSpeed 기본값 포함
 *
 * @param el - 정렬 가능하게 만들 요소의 ref
 * @param list - 정렬할 데이터 배열의 ref
 * @param options - Sortable.js 옵션 (선택사항, 위 가이드 참조)
 * @returns useSortable의 반환값
 */
export function useSortableList<T = any>(
  el: Ref<HTMLElement | null | undefined>,
  list: Ref<T[]>,
  options?: UseSortableOptions
) {
  // 기본 옵션 설정
  const defaultOptions: UseSortableOptions = {
    animation: 150, // 애니메이션 속도 (ms)
    scrollSensitivity: 50, // 가장자리에서 50px 이내로 가면 스크롤 시작
    scrollSpeed: 20, // 스크롤 속도
    scroll: true, // 자동 스크롤 활성화
    forceAutoScrollFallback: true, // 모든 브라우저에서 자동 스크롤 강제 활성화
  };

  // 사용자 옵션과 병합
  const mergedOptions = {
    ...defaultOptions,
    ...options,
  };

  return useSortable(el, list, mergedOptions);
}

/**
 * 커스텀 옵션으로 Sortable 설정
 *
 * @description
 * - 기본 옵션 없이 완전한 커스터마이징이 필요한 경우 사용
 * - useSortable을 직접 래핑하여 모든 옵션을 직접 제어
 * - useSortableList와 달리 기본값이 없으므로 필요한 모든 옵션을 명시해야 함
 *
 * @example Handle 옵션 사용
 * @example Group 옵션 - 여러 리스트 간 이동
 * @example Filter & Disabled 옵션
 * @example Direction & Swap 옵션
 *
 * @param el - 정렬 가능하게 만들 요소의 ref
 * @param list - 정렬할 데이터 배열의 ref
 * @param options - Sortable.js 옵션 (위 가이드의 모든 옵션 사용 가능)
 * @returns useSortable의 반환값
 */
export function useSortableCustom<T = any>(
  el: Ref<HTMLElement | null | undefined>,
  list: Ref<T[]>,
  options: UseSortableOptions
) {
  return useSortable(el, list, options);
}

/**
 * 기본 Sortable 옵션 상수
 *
 * @description
 * 프로젝트 전체에서 사용할 수 있는 기본 Sortable.js 옵션
 * - animation: 150ms - 적당한 애니메이션 속도
 * - scrollSensitivity: 50px - 중간 감도
 * - scrollSpeed: 20 - 보통 스크롤 속도
 * - scroll: true, - 자동 스크롤 활성화
 * - forceAutoScrollFallback: true, - 모든 브라우저에서 자동 스크롤 강제 활성화
 */
export const DEFAULT_SORTABLE_OPTIONS: UseSortableOptions = {
  animation: 150,
  scrollSensitivity: 50,
  scrollSpeed: 20,
  scroll: true, // 자동 스크롤 활성화
  forceAutoScrollFallback: true, // 모든 브라우저에서 자동 스크롤 강제 활성화
};
