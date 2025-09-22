<!-- components/SCSwiper.vue -->
<template>
  <div 
    :class="`sc-swiper-container sc-swiper-${uniqueSwiperId}`"
    :role="ariaRole"
    :aria-label="computedAriaLabel"
  >
    <!-- Swiper 컨테이너 -->
    <swiper 
      ref="swiperRef"
      :class="`swiper swiper-${uniqueSwiperId}`"
      v-bind="swiperConfig"
      @swiper="onSwiperInit"
      @slideChange="onSlideChange"
      @progress="onProgress"
    >
      <!-- 데이터 기반 슬라이드 렌더링 (slides prop 사용 시) -->
      <template v-if="props.slides && props.slides.length > 0">
        <swiper-slide 
          v-for="(slide, index) in props.slides" 
          :key="slide.id || index"
        >
          <slot name="slide" :item="slide" :index="index">
            <!-- 기본 슬라이드 템플릿 -->
            <div class="sc-swiper-slide-default">
              <h3 v-if="slide.title">{{ slide.title }}</h3>
              <p v-if="slide.description">{{ slide.description }}</p>
              <img v-if="slide.image" :src="slide.image" :alt="slide.title || `Slide ${index + 1}`" />
            </div>
          </slot>
        </swiper-slide>
      </template>
      
      <!-- 템플릿 기반 슬라이드 (SwiperSlide 직접 사용 시) -->
      <template v-else>
        <slot />
      </template>
    </swiper>
    
    <!-- Pagination -->
    <div 
      v-if="shouldShowPagination" 
      :class="`swiper-pagination swiper-pagination-${uniqueSwiperId}`"
    ></div>
    
    <!-- Navigation -->
    <div 
      v-if="shouldShowNavigation"
      :class="`swiper-button-prev swiper-button-prev-${uniqueSwiperId}`"
    ></div>
    <div 
      v-if="shouldShowNavigation"
      :class="`swiper-button-next swiper-button-next-${uniqueSwiperId}`"
    ></div>
    
    <!-- Scrollbar -->
    <div 
      v-if="shouldShowScrollbar" 
      :class="`swiper-scrollbar swiper-scrollbar-${uniqueSwiperId}`"
    ></div>
    
    <!-- Screen Reader 전용 정보 -->
    <div class="sr-only" aria-live="polite" aria-atomic="true">
      현재 {{ currentSlideIndex + 1 }}번째 슬라이드, 총 {{ totalSlides }}개
    </div>
  </div>
</template>

<script setup lang="ts">
import { 
  ref, 
  computed, 
  onMounted, 
  onUnmounted,
  onErrorCaptured, 
  shallowRef, 
  watchEffect,
  nextTick,
  markRaw
} from 'vue';
import { Swiper, SwiperSlide } from 'swiper/vue';

// ============================================================================
// TYPES
// ============================================================================
export type PaginationType = 'bullets' | 'fraction' | 'progressbar' | 'custom';

export interface PaginationConfig {
  el?: string | HTMLElement;
  type?: PaginationType;
  bulletElement?: string;
  bulletClass?: string;
  bulletActiveClass?: string;
  modifierClass?: string;
  currentClass?: string;
  totalClass?: string;
  hiddenClass?: string;
  progressbarOpposite?: boolean;
  progressbarFillClass?: string;
  clickable?: boolean;
  hideOnClick?: boolean;
  renderBullet?: (index: number, className: string) => string;
  renderFraction?: (currentClass: string, totalClass: string) => string;
  renderProgressbar?: (progressbarFillClass: string) => string;
  renderCustom?: (swiper: any, current: number, total: number) => string;
}

export interface NavigationConfig {
  nextEl?: string | HTMLElement;
  prevEl?: string | HTMLElement;
  hideOnClick?: boolean;
  disabledClass?: string;
  hiddenClass?: string;
  lockClass?: string;
}

export interface ScrollbarConfig {
  el?: string | HTMLElement;
  hide?: boolean;
  draggable?: boolean;
  snapOnRelease?: boolean;
  dragSize?: number | 'auto';
}

export interface AutoplayConfig {
  delay?: number;
  reverseDirection?: boolean;
  disableOnInteraction?: boolean;
  pauseOnMouseEnter?: boolean;
  stopOnLastSlide?: boolean;
  waitForTransition?: boolean;
}

export interface SCSwiperProps {
  // 데이터 기반 props (ScSwiper용)
  slides?: any[];
  
  // 공통 Swiper 설정
  pagination?: boolean | PaginationType | PaginationConfig;
  paginationType?: PaginationType;
  navigation?: boolean | NavigationConfig;
  scrollbar?: boolean | ScrollbarConfig;
  autoplay?: boolean | AutoplayConfig;
  loop?: boolean;
  slidesPerView?: number | 'auto';
  spaceBetween?: number;
  centeredSlides?: boolean;
  direction?: 'horizontal' | 'vertical';
  speed?: number;
  effect?: 'slide' | 'fade' | 'cube' | 'coverflow' | 'flip';
  breakpoints?: { [key: number]: any };
  uniqueId?: string;
  wrapperClass?: string;
  ariaLabel?: string;
  debug?: boolean;
  exposeInstance?: boolean;
}

// ============================================================================
// UTILITIES
// ============================================================================
let idCounter = 0;
const generateUniqueId = (prefix = 'swiper'): string => {
  return `${prefix}-${++idCounter}-${Date.now()}`;
};

// 공통 모듈 설정 팩토리 함수
const createModuleConfig = <T>(
  config: boolean | T,
  baseConfig: T
): T | false => {
  if (config === false) return false;
  if (config === true) return baseConfig;
  return { ...baseConfig, ...config };
};

const createPaginationConfig = (
  pagination: boolean | PaginationType | PaginationConfig,
  paginationType: PaginationType | undefined,
  elementSelector: string
): PaginationConfig | false => {
  if (pagination === false) return false;
  
  const baseConfig: PaginationConfig = {
    el: elementSelector,
    clickable: true,
  };
  
  // 1. pagination이 객체인 경우 (가장 우선순위)
  if (typeof pagination === 'object' && pagination !== null) {
    return { ...baseConfig, ...pagination };
  }
  
  // 2. pagination이 문자열인 경우
  if (typeof pagination === 'string') {
    return { ...baseConfig, type: pagination };
  }
  
  // 3. paginationType이 명시된 경우
  if (paginationType) {
    return { ...baseConfig, type: paginationType };
  }
  
  // 4. pagination이 true인 경우 (기본값)
  if (pagination === true) {
    return { ...baseConfig, type: 'bullets' };
  }
  
  return false;
};

const createNavigationConfig = (
  navigation: boolean | NavigationConfig,
  uniqueId: string
): NavigationConfig | false => {
  const baseConfig: NavigationConfig = {
    nextEl: `.swiper-button-next-${uniqueId}`,
    prevEl: `.swiper-button-prev-${uniqueId}`
  };
  
  return createModuleConfig(navigation, baseConfig);
};

const createScrollbarConfig = (
  scrollbar: boolean | ScrollbarConfig,
  uniqueId: string
): ScrollbarConfig | false => {
  const baseConfig: ScrollbarConfig = {
    el: `.swiper-scrollbar-${uniqueId}`,
    draggable: true
  };
  
  return createModuleConfig(scrollbar, baseConfig);
};

const validateSwiperProps = (props: SCSwiperProps): void => {
  // 기본 검증 (항상 실행)
  if (typeof props.slidesPerView === 'number' && props.slidesPerView <= 0) {
    throw new Error('[SCSwiper] slidesPerView must be positive number');
  }
  
  if (typeof props.speed === 'number' && props.speed < 0) {
    throw new Error('[SCSwiper] speed must be non-negative');
  }
  
  if (typeof props.spaceBetween === 'number' && props.spaceBetween < 0) {
    throw new Error('[SCSwiper] spaceBetween must be non-negative');
  }
  
  // 디버그 모드 추가 검증
  if (props.debug) {
    console.log('[SCSwiper] Props validation passed:', {
      slidesPerView: props.slidesPerView,
      speed: props.speed,
      spaceBetween: props.spaceBetween
    });
  }
};

// 모듈 캐시 (성능 최적화)
const moduleCache = markRaw({
  Pagination: null as any,
  Navigation: null as any,
  Scrollbar: null as any,
  Autoplay: null as any,
  EffectFade: null as any,
  EffectCube: null as any,
  EffectCoverflow: null as any,
  EffectFlip: null as any,
});

const getRequiredModules = async (props: any) => {
  const modules = [];
  
  if (props.pagination && !moduleCache.Pagination) {
    const { Pagination } = await import('swiper/modules');
    moduleCache.Pagination = markRaw(Pagination);
  }
  if (props.pagination) modules.push(moduleCache.Pagination);
  
  if (props.navigation && !moduleCache.Navigation) {
    const { Navigation } = await import('swiper/modules');
    moduleCache.Navigation = markRaw(Navigation);
  }
  if (props.navigation) modules.push(moduleCache.Navigation);
  
  if (props.scrollbar && !moduleCache.Scrollbar) {
    const { Scrollbar } = await import('swiper/modules');
    moduleCache.Scrollbar = markRaw(Scrollbar);
  }
  if (props.scrollbar) modules.push(moduleCache.Scrollbar);
  
  if (props.autoplay && !moduleCache.Autoplay) {
    const { Autoplay } = await import('swiper/modules');
    moduleCache.Autoplay = markRaw(Autoplay);
  }
  if (props.autoplay) modules.push(moduleCache.Autoplay);
  
  // Effect 모듈들
  const effectModuleMap = {
    'fade': 'EffectFade',
    'cube': 'EffectCube',
    'coverflow': 'EffectCoverflow',
    'flip': 'EffectFlip'
  } as const;
  
  const effectModule = effectModuleMap[props.effect as keyof typeof effectModuleMap];
  if (effectModule && !moduleCache[effectModule]) {
    const module = await import('swiper/modules');
    moduleCache[effectModule] = markRaw(module[effectModule]);
  }
  if (effectModule) modules.push(moduleCache[effectModule]);
  
  return markRaw(modules);
};

// ============================================================================
// COMPONENT LOGIC
// ============================================================================

// Props 정의
interface Props extends SCSwiperProps {}

const props = withDefaults(defineProps<Props>(), {
  slides: () => [],
  pagination: true,
  paginationType: undefined,
  navigation: false,
  scrollbar: false,
  autoplay: false,
  loop: false,
  slidesPerView: 1,
  spaceBetween: 0,
  centeredSlides: false,
  direction: 'horizontal',
  speed: 300,
  effect: 'slide',
  debug: false,
  exposeInstance: false,
});

// Emits 정의
const emit = defineEmits<{
  slideChange: [{ activeIndex: number, realIndex: number }];
  progress: [{ progress: number }];
  init: [any];
  error: [Error];
  beforeSlideChange: [{ from: number, to: number }];
  afterSlideChange: [{ activeIndex: number }];
  reachEnd: [];
  reachBeginning: [];
}>(); 

// 반응형 참조
const swiperRef = shallowRef<any>(null);
const swiperInstance = shallowRef<any>(null);
const currentSlideIndex = ref(0);
const totalSlides = ref(0);
const scrollProgress = ref(0);
const isAtStart = ref(true);
const isAtEnd = ref(false);
const modules = shallowRef<any[]>([]);

// 고유 ID (성능 최적화)
const uniqueSwiperId = computed(() => 
  props.uniqueId || generateUniqueId('swiper')
);

// 접근성 속성
const ariaRole = computed(() => 'region');
const computedAriaLabel = computed(() => 
  props.ariaLabel || `Swiper carousel with ${totalSlides.value} slides`
);

// 표시 여부 (메모이제이션)
const shouldShowPagination = computed(() => props.pagination !== false);
const shouldShowNavigation = computed(() => props.navigation !== false);
const shouldShowScrollbar = computed(() => props.scrollbar !== false);

// 설정 객체들
const paginationConfig = computed(() => 
  createPaginationConfig(
    props.pagination,
    props.paginationType,
    `.swiper-pagination-${uniqueSwiperId.value}`
  )
);

const navigationConfig = computed(() => 
  createNavigationConfig(props.navigation, uniqueSwiperId.value)
);

const scrollbarConfig = computed(() => 
  createScrollbarConfig(props.scrollbar, uniqueSwiperId.value)
);

const autoplayConfig = computed(() => {
  if (props.autoplay === false) return false;
  
  const baseConfig = {
    delay: 3000,
    disableOnInteraction: false,
  };
  
  if (props.autoplay === true) return baseConfig;
  return { ...baseConfig, ...props.autoplay };
});

// 최종 Swiper 설정 (성능 최적화)
const swiperConfig = computed(() => ({
  modules: modules.value,
  pagination: paginationConfig.value,
  navigation: navigationConfig.value,
  scrollbar: scrollbarConfig.value,
  autoplay: autoplayConfig.value,
  loop: props.loop,
  slidesPerView: props.slidesPerView,
  spaceBetween: props.spaceBetween,
  centeredSlides: props.centeredSlides,
  direction: props.direction,
  speed: props.speed,
  effect: props.effect,
  breakpoints: props.breakpoints,
  wrapperClass: props.wrapperClass,
  a11y: {
    enabled: true,
    prevSlideMessage: 'Previous slide',
    nextSlideMessage: 'Next slide',
    firstSlideMessage: 'This is the first slide',
    lastSlideMessage: 'This is the last slide',
    paginationBulletMessage: 'Go to slide {{index}}',
  },
}));

// 이벤트 핸들러들
const onSwiperInit = (swiper: any) => {
  swiperInstance.value = swiper;
  totalSlides.value = swiper.slides.length;
  emit('init', swiper);
  
  if (props.debug) {
    console.log(`[SCSwiper ${uniqueSwiperId.value}] Initialized with ${totalSlides.value} slides`);
  }
};

const onSlideChange = (swiper: any) => {
  const previousIndex = currentSlideIndex.value;
  currentSlideIndex.value = swiper.activeIndex;
  isAtStart.value = swiper.isBeginning;
  isAtEnd.value = swiper.isEnd;
  
  // 이벤트 발생
  emit('slideChange', {
    activeIndex: swiper.activeIndex,
    realIndex: swiper.realIndex
  });
  
  emit('afterSlideChange', { activeIndex: swiper.activeIndex });
  
  // 시작/끝 도달 이벤트
  if (swiper.isBeginning) {
    emit('reachBeginning');
  }
  if (swiper.isEnd) {
    emit('reachEnd');
  }
};

const onProgress = (swiper: any, progress: number) => {
  scrollProgress.value = Math.round(progress * 100);
  emit('progress', { progress });
};

// 에러 처리
onErrorCaptured((error) => {
  console.error(`[SCSwiper ${uniqueSwiperId.value}] Error:`, error);
  emit('error', error);
  return false;
});

// 초기화
onMounted(async () => {
  try {
    validateSwiperProps(props);
    modules.value = await getRequiredModules(props);
    
    await nextTick();
    
    if (props.debug) {
      console.log(`[SCSwiper ${uniqueSwiperId.value}] Mounted with modules:`, modules.value);
    }
  } catch (error) {
    console.error(`[SCSwiper ${uniqueSwiperId.value}] Mount error:`, error);
    emit('error', error as Error);
  }
});

// 메모리 누수 방지
onUnmounted(() => {
  if (swiperInstance.value) {
    try {
      swiperInstance.value.destroy(true, true);
      if (props.debug) {
        console.log(`[SCSwiper ${uniqueSwiperId.value}] Destroyed`);
      }
    } catch (error) {
      console.error(`[SCSwiper ${uniqueSwiperId.value}] Destroy error:`, error);
    }
  }
});

// Props 변경 감지 (성능 최적화)
watchEffect(() => {
  if (swiperInstance.value) {
    swiperInstance.value.update();
  }
});

// 외부에서 접근 가능한 메서드들
defineExpose({
  swiper: swiperInstance,
  slideTo: (index: number) => swiperInstance.value?.slideTo(index),
  slideNext: () => swiperInstance.value?.slideNext(),
  slidePrev: () => swiperInstance.value?.slidePrev(),
  update: () => swiperInstance.value?.update(),
  
  // 추가: 외부 컴포넌트에서 필요한 상태들
  currentSlideIndex: computed(() => currentSlideIndex.value),
  totalSlides: computed(() => totalSlides.value),
  isAtStart: computed(() => isAtStart.value),
  isAtEnd: computed(() => isAtEnd.value),
  uniqueId: computed(() => uniqueSwiperId.value),
});
</script>

<style scoped>
.sc-swiper-container {
  position: relative;
  width: 100%;
}

/* 접근성을 위한 Screen Reader 전용 클래스 */
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

/* 기본 슬라이드 스타일 */
.sc-swiper-slide-default {
  padding: 20px;
  text-align: center;
  background: #f8f9fa;
  border-radius: 8px;
}

.sc-swiper-slide-default h3 {
  margin: 0 0 12px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.sc-swiper-slide-default p {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.sc-swiper-slide-default img {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
}
</style>

<!--
===========================================
사용법 예시:
===========================================

예시 템플릿:
  <!-- 데이터 기반 사용법 (추천) -->
  <SCSwiper 
    :slides="slides" 
    pagination="bullets" 
    :navigation="true"
  >
    <template #slide="{ item, index }">
      <div class="custom-slide">
        <h3>{{ item.title }}</h3>
        <p>{{ item.description }}</p>
      </div>
    </template>
  </SCSwiper>

  <!-- 기본 슬라이드 템플릿 사용 (slides만 제공) -->
  <SCSwiper :slides="slides" pagination="bullets" :navigation="true" />

  <!-- 템플릿 기반 사용법 (SwiperSlide 직접 사용) -->
  <SCSwiper pagination="bullets" :navigation="true">
    <SwiperSlide>Slide 1</SwiperSlide>
    <SwiperSlide>Slide 2</SwiperSlide>
  </SCSwiper>

  <!-- 타입 분리 -->
  <SCSwiper :slides="slides" :pagination="true" pagination-type="fraction" />

  <!-- 상세 설정 -->
  <SCSwiper 
    :slides="slides"
    :pagination="{ 
      type: 'bullets', 
      clickable: true,
      bulletClass: 'custom-bullet' 
    }"
    :navigation="true"
    :autoplay="{ delay: 3000 }"
    :loop="true"
  >
    <template #slide="{ item, index }">
      <div class="premium-slide">
        <h2>{{ item.title }}</h2>
        <p>{{ item.description }}</p>
        <img v-if="item.image" :src="item.image" :alt="item.title" />
      </div>
    </template>
  </SCSwiper>

<script setup>
import { SwiperSlide } from 'swiper/vue';
import SCSwiper from '@/components/SCSwiper.vue';

const slides = [
  { id: 1, title: "슬라이드 1", description: "첫 번째 슬라이드" },
  { id: 2, title: "슬라이드 2", description: "두 번째 슬라이드" },
  { id: 3, title: "슬라이드 3", description: "세 번째 슬라이드" },
];

===========================================
필요한 CSS Import (main.ts 또는 App.vue):
===========================================

import 'swiper/css';
import 'swiper/css/navigation';
import 'swiper/css/pagination';
import 'swiper/css/scrollbar';

-->
