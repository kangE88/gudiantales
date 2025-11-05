import type { UseSortableOptions } from '@vueuse/integrations/useSortable'
import { useSortable } from '@vueuse/integrations/useSortable'
import type { Ref } from 'vue'

/**
 * 드래그 앤 드롭으로 리스트 정렬을 가능하게 하는 공통 함수
 * 
 * @description
 * - Sortable.js를 기반으로 한 VueUse의 useSortable을 래핑
 * - 스크롤 컨테이너 내에서 자동 스크롤 기능을 포함한 기본 설정 제공
 * 
 * @example
 * ```vue
 * <script setup>
 * import { ref } from 'vue'
 * import { useSortableList } from '@shc-nss/shared'
 * 
 * const list = ref([
 *   { id: 1, name: 'Item 1' },
 *   { id: 2, name: 'Item 2' },
 * ])
 * const el = ref()
 * 
 * useSortableList(el, list)
 * </script>
 * 
 * <template>
 *   <div ref="el">
 *     <div v-for="item in list" :key="item.id">{{ item.name }}</div>
 *   </div>
 * </template>
 * ```
 * 
 * @param el - 정렬 가능하게 만들 요소의 ref
 * @param list - 정렬할 데이터 배열의 ref
 * @param options - Sortable.js 옵션 (선택사항)
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
    scroll: true, // 자동 스크롤 활성화
    forceAutoScrollFallback: true, // 모든 브라우저에서 자동 스크롤 강제 활성화
    scrollSensitivity: 50, // 가장자리에서 50px 이내로 가면 스크롤 시작
    scrollSpeed: 20, // 스크롤 속도
    bubbleScroll: true, // 부모 요소도 스크롤 가능
  }

  // 사용자 옵션과 병합
  const mergedOptions = {
    ...defaultOptions,
    ...options,
  }

  return useSortable(el, list, mergedOptions)
}

/**
 * 커스텀 옵션으로 Sortable 설정
 * 
 * @description
 * - 기본 옵션 없이 완전한 커스터마이징이 필요한 경우 사용
 * - useSortable을 직접 래핑
 * 
 * @example
 * ```vue
 * <script setup>
 * import { ref } from 'vue'
 * import { useSortableCustom } from '@shc-nss/shared'
 * 
 * const list = ref([...])
 * const el = ref()
 * 
 * useSortableCustom(el, list, {
 *   animation: 300,
 *   handle: '.drag-handle',
 *   ghostClass: 'ghost',
 * })
 * </script>
 * ```
 * 
 * @param el - 정렬 가능하게 만들 요소의 ref
 * @param list - 정렬할 데이터 배열의 ref
 * @param options - Sortable.js 옵션
 * @returns useSortable의 반환값
 */
export function useSortableCustom<T = any>(
  el: Ref<HTMLElement | null | undefined>,
  list: Ref<T[]>,
  options: UseSortableOptions
) {
  return useSortable(el, list, options)
}

/**
 * 기본 Sortable 옵션 상수
 * 
 * @description
 * 프로젝트 전체에서 사용할 수 있는 기본 Sortable.js 옵션
 */
export const DEFAULT_SORTABLE_OPTIONS: UseSortableOptions = {
  animation: 150,
  scroll: true,
  forceAutoScrollFallback: true,
  scrollSensitivity: 50,
  scrollSpeed: 20,
  bubbleScroll: true,
}

/**
 * 빠른 스크롤을 위한 Sortable 옵션
 * 
 * @description
 * 긴 리스트에서 빠른 스크롤이 필요한 경우 사용
 */
export const FAST_SCROLL_OPTIONS: UseSortableOptions = {
  animation: 150,
  scroll: true,
  forceAutoScrollFallback: true,
  scrollSensitivity: 80,
  scrollSpeed: 40,
  bubbleScroll: true,
}

/**
 * 느린 스크롤을 위한 Sortable 옵션
 * 
 * @description
 * 정밀한 조작이 필요한 경우 사용
 */
export const SLOW_SCROLL_OPTIONS: UseSortableOptions = {
  animation: 200,
  scroll: true,
  forceAutoScrollFallback: true,
  scrollSensitivity: 30,
  scrollSpeed: 10,
  bubbleScroll: true,
}

