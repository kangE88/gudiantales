<!-- components/SCSwiper.vue -->
<template>
    <div 
      :class="[containerClasses, `sc-swiper-${swiperId}`]"
      :data-effect="props.effect"
      :role="ariaRole"
      :aria-label="computedAriaLabel"
    >
      <!-- Swiper 컨테이너 -->
      <swiper 
        ref="swiperRef"
        :class="`swiper swiper-${swiperId}`"
        :modules="modules"
        :pagination="paginationConfig"
        :navigation="navigationConfig"
        :scrollbar="scrollbarConfig"
        :autoplay="autoplayConfig"
        :controller="controllerConfig !== false ? controllerConfig : undefined"
        :loop="props.loop"
        :slidesPerView="adjustedSlidesPerView"
        :spaceBetween="props.spaceBetween"
        :centeredSlides="props.effect === 'cylinder' ? true : props.centeredSlides"
        :direction="props.direction"
        :speed="props.speed"
        :effect="props.effect"
        :debug="props.debug"
        :cubeEffect="props.effect === 'cube' ? effectConfig.cubeEffect : undefined"
        :fadeEffect="props.effect === 'fade' ? effectConfig.fadeEffect : undefined"
        :coverflowEffect="(props.effect === 'coverflow' || props.effect === 'cylinder') ? effectConfig.coverflowEffect : undefined"
        :flipEffect="props.effect === 'flip' ? effectConfig.flipEffect : undefined"
        :cardsEffect="props.effect === 'cards' ? effectConfig.cardsEffect : undefined"
        :creativeEffect="props.effect === 'creative' ? effectConfig.creativeEffect : undefined"
        :breakpoints="props.breakpoints"
        :wrapperClass="props.wrapperClass"
        @swiper="onSwiperInit"
        @slideChange="onSlideChange"
        @progress="onProgress"
        @click="onSlideClick"
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
        :class="`swiper-pagination swiper-pagination-${swiperId}`"
      ></div>
      
      <!-- Navigation -->
      <div 
        v-if="shouldShowNavigation"
        :class="`swiper-button-prev swiper-button-prev-${swiperId}`"
      ></div>
      <div 
        v-if="shouldShowNavigation"
        :class="`swiper-button-next swiper-button-next-${swiperId}`"
      ></div>
      
      <!-- Scrollbar -->
      <div 
        v-if="shouldShowScrollbar" 
        :class="`swiper-scrollbar swiper-scrollbar-${swiperId}`"
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
import type { SwiperVariantProps } from './swiper.variants';
import { SwiperVariants } from './swiper.variants';
  
  // Swiper CSS import
  import 'swiper/css';
  import 'swiper/css/navigation';
  import 'swiper/css/pagination';
  import 'swiper/css/scrollbar';
  import 'swiper/css/effect-fade';
  import 'swiper/css/effect-cube';
  import 'swiper/css/effect-coverflow';
  import 'swiper/css/effect-flip';
  import 'swiper/css/effect-cards';
  import 'swiper/css/effect-creative'; 
  
  
  // Swiper 모듈들을 전역 등록
  import SwiperCore from 'swiper';
  import { Navigation, Pagination, Scrollbar, Autoplay, Controller, EffectFade, EffectCube, EffectCoverflow, EffectFlip, EffectCards, EffectCreative } from 'swiper/modules';
  
  // 모든 모듈 등록
  SwiperCore.use([Navigation, Pagination, Scrollbar, Autoplay, Controller, EffectFade, EffectCube, EffectCoverflow, EffectFlip, EffectCards, EffectCreative]);
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
  
export interface ControllerConfig {
  control?: any; // 제어할 다른 Swiper 인스턴스
  inverse?: boolean; // 역방향 제어
  by?: 'slide' | 'container'; // 제어 방식
}

// TypeScript 오류 수정: Effect 설정을 위한 인터페이스 정의
export interface EffectSettings {
  cubeEffect?: {
    shadow?: boolean;
    slideShadows?: boolean;
    shadowOffset?: number;
    shadowScale?: number;
  };
  coverflowEffect?: {
    rotate?: number;
    stretch?: number;
    depth?: number;
    modifier?: number;
    slideShadows?: boolean;
    scale?: number;
  };
  flipEffect?: {
    slideShadows?: boolean;
    limitRotation?: boolean;
  };
  fadeEffect?: {
    crossFade?: boolean;
  };
  cardsEffect?: {
    slideShadows?: boolean;
    perSlideOffset?: number;
    perSlideRotate?: number;
    rotate?: boolean;
  };
  creativeEffect?: {
    prev?: {
      shadow?: boolean;
      translate?: [string, number, number];
      rotate?: [number, number, number];
    };
    next?: {
      shadow?: boolean;
      translate?: [string, number, number];
      rotate?: [number, number, number];
    };
  };
  // 인덱스 시그니처 추가: 동적 속성 접근을 위한 타입 안전성 제공
  [key: string]: any;
}
  
  export interface SCSwiperProps {
    /**
     * 슬라이드 데이터 배열
     * @description 각 슬라이드에 표시할 데이터 객체들의 배열
     * @example [{ id: 1, title: "슬라이드 1", image: "image.jpg" }]
     */
    slides?: any[];
    
    /**
     * 페이지네이션 설정
     * @description 페이지네이션 표시 여부 및 상세 설정
     * @default true
     * @example true | false | 'bullets' | { el: '.pagination', clickable: true }
     */
    pagination?: boolean | PaginationType | PaginationConfig;
    
    /**
     * 페이지네이션 타입
     * @description 페이지네이션의 표시 형태
     * @default 'bullets'
     * @example 'bullets' | 'fraction' | 'progressbar' | 'custom'
     */
    paginationType?: PaginationType;
    
    /**
     * 네비게이션 버튼 설정
     * @description 이전/다음 버튼 표시 여부 및 상세 설정
     * @default true
     * @example true | false | { nextEl: '.next', prevEl: '.prev' }
     */
    navigation?: boolean | NavigationConfig;
    
    /**
     * 스크롤바 설정
     * @description 스크롤바 표시 여부 및 상세 설정
     * @default false
     * @example true | false | { el: '.scrollbar', draggable: true }
     */
    scrollbar?: boolean | ScrollbarConfig;
    
    /**
     * 자동재생 설정
     * @description 자동재생 활성화 여부 및 상세 설정
     * @default false
     * @example true | false | { delay: 3000, disableOnInteraction: false }
     */
    autoplay?: boolean | AutoplayConfig;
    
    /**
     * 무한 루프 여부
     * @description 마지막 슬라이드에서 첫 번째 슬라이드로 순환
     * @default false
     */
    loop?: boolean;
    
    /**
     * 한 번에 보이는 슬라이드 수
     * @description 화면에 동시에 표시되는 슬라이드의 개수
     * @default 1
     * @example 1 | 2 | 3 | 'auto'
     */
    slidesPerView?: number | 'auto';
    
    /**
     * 슬라이드 간격
     * @description 슬라이드 사이의 간격 (픽셀 단위)
     * @default 0
     * @example 10 | 20 | 30
     */
    spaceBetween?: number;
    
    /**
     * 중앙 정렬 여부
     * @description 활성 슬라이드를 중앙에 배치할지 여부
     * @default false
     */
    centeredSlides?: boolean;
    
    /**
     * 슬라이드 방향
     * @description 슬라이드가 이동하는 방향
     * @default 'horizontal'
     * @example 'horizontal' | 'vertical'
     */
    direction?: 'horizontal' | 'vertical';
    
    /**
     * 전환 속도
     * @description 슬라이드 전환 애니메이션 속도 (밀리초)
     * @default 300
     * @example 300 | 500 | 1000
     */
    speed?: number;
    
    /**
     * 전환 효과
     * @description 슬라이드 전환 시 사용할 시각적 효과
     * @default 'slide'
     * @example 'slide' | 'fade' | 'cube' | 'coverflow' | 'flip' | 'cards' | 'creative' | 'cylinder'
     */
    effect?: 'slide' | 'fade' | 'cube' | 'coverflow' | 'flip'| 'cards'| 'creative' | 'cylinder';
    
    /**
     * 반응형 설정
     * @description 화면 크기별 설정을 정의하는 객체
     * @example { 768: { slidesPerView: 2 }, 1024: { slidesPerView: 3 } }
     */
    breakpoints?: { [key: number]: any };
    
    /**
     * 컨트롤러 설정
     * @description 다른 Swiper 인스턴스와의 연동 여부
     * @default false
     * @example true | false | { control: otherSwiper, inverse: true }
     */
    controller?: boolean | ControllerConfig;
    
    /**
     * 컨트롤러 그룹명
     * @description 같은 그룹의 Swiper들끼리 연동하기 위한 식별자
     * @example 'group1' | 'main-gallery'
     */
    controllerGroup?: string;
    
    /**
     * Swiper 고유 ID
     * @description Swiper 인스턴스의 고유 식별자
     * @example 'main-swiper' | 'gallery-1'
     */
    swiperId?: string;
    
    /**
     * 래퍼 클래스명
     * @description Swiper 래퍼에 추가할 CSS 클래스
     * @example 'custom-wrapper' | 'gallery-wrapper'
     */
    wrapperClass?: string;
    
    /**
     * 접근성 라벨
     * @description 스크린 리더를 위한 aria-label 속성
     * @default 'Swiper carousel'
     * @example 'Product gallery' | 'Image carousel'
     */
    ariaLabel?: string;
    
    /**
     * 디버그 모드
     * @description 개발 시 콘솔에 디버그 정보 출력 여부
     * @default false
     */
    debug?: boolean;
    
    /**
     * 인스턴스 노출 여부
     * @description Swiper 인스턴스를 부모 컴포넌트에서 접근 가능하게 할지 여부
     * @default false
     */
    exposeInstance?: boolean;
    
    // ============================================================================
    // VARIANTS 연동 PROPS
    // ============================================================================
    
    /**
     * 크기 variant
     * @description Swiper 컨테이너의 크기 설정
     * @default 'medium'
     * @example 'small' | 'medium' | 'large' | 'xlarge'
     */
    size?: SwiperVariantProps['size'];
    
    /**
     * 테마 variant  
     * @description Swiper의 시각적 테마 설정
     * @default 'default'
     * @example 'default' | 'dark' | 'light' | 'minimal' | 'colorful'
     */
    theme?: SwiperVariantProps['theme'];
    
    /**
     * 네비게이션 스타일 variant
     * @description 네비게이션 버튼의 스타일 타입
     * @default 'default'
     * @example 'default' | 'arrows' | 'minimal' | 'rounded' | 'square'
     */
    navigationStyle?: SwiperVariantProps['navigationStyle'];
    
    /**
     * 페이지네이션 스타일 variant
     * @description 페이지네이션의 스타일 타입  
     * @default 'default'
     * @example 'default' | 'minimal' | 'rounded' | 'line' | 'fraction'
     */
    paginationStyle?: SwiperVariantProps['paginationStyle'];
    
    /**
     * 상태 variant
     * @description Swiper의 현재 상태를 나타내는 클래스
     * @default 'normal'
     * @example 'normal' | 'loading' | 'error' | 'empty'
     */
    state?: SwiperVariantProps['state'];
    
    /**
     * 간격 variant
     * @description 슬라이드 간격의 미리 정의된 값들
     * @default 'normal'
     * @example 'none' | 'tight' | 'normal' | 'loose' | 'wide'
     */
    spacing?: SwiperVariantProps['spacing'];
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
    
    // 디버그 로그 추가
    if (props.debug) {
      console.log(`[createPaginationConfig] pagination:`, pagination);
      console.log(`[createPaginationConfig] paginationType:`, paginationType);
    }
    
    // 1. pagination이 객체인 경우 (가장 우선순위)
    if (typeof pagination === 'object' && pagination !== null) {
      const config = { ...baseConfig, ...pagination };
      if (props.debug) console.log(`[createPaginationConfig] Object config:`, config);
      return config;
    }
    
    // 2. pagination이 문자열인 경우
    if (typeof pagination === 'string') {
      const config = { ...baseConfig, type: pagination };
      if (props.debug) console.log(`[createPaginationConfig] String config:`, config);
      return config;
    }
    
    // 3. paginationType이 명시된 경우 (이게 우리의 주요 케이스)
    if (paginationType) {
      const config = { ...baseConfig, type: paginationType };
      if (props.debug) console.log(`[createPaginationConfig] PaginationType config:`, config);
      return config;
    }
    
    // 4. pagination이 true인 경우 (기본값)
    if (pagination === true) {
      // TypeScript 오류 수정: type을 명시적으로 PaginationType으로 캐스팅
      const config = { ...baseConfig, type: 'bullets' as PaginationType };
      if (props.debug) console.log(`[createPaginationConfig] Default config:`, config);
      return config;
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
  
  const createControllerConfig = (
    controller: boolean | ControllerConfig,
    controllerGroup?: string,
    currentInstance?: any
  ): ControllerConfig | false => {
    if (controller === false) return false;
    
    const baseConfig: ControllerConfig = {
      by: 'slide',
      inverse: false
    };
    
    if (controller === true) {
      // controllerGroup이 있으면 그룹 내 다른 인스턴스들과 연동
      if (controllerGroup && currentInstance) {
        const groupInstances = controllerGroups.get(controllerGroup) || [];
        const otherInstances = groupInstances.filter(instance => instance !== currentInstance);
        if (otherInstances.length > 0) {
          baseConfig.control = otherInstances;
        }
      }
      return baseConfig;
    }
    
    return { ...baseConfig, ...controller };
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
    Controller: null as any, // Controller 모듈 추가
    EffectFade: null as any,
    EffectCube: null as any,
    EffectCoverflow: null as any,
    EffectFlip: null as any,
    EffectCards: null as any,
    EffectCreative: null as any,
  });
  
  // Controller 그룹 관리 (전역)
  const controllerGroups = markRaw(new Map<string, any[]>());
  
  const getRequiredModules = async (props: any) => {
    const modules = [];
    
    if (props.debug) {
      console.log(`[getRequiredModules] Using globally registered modules for effect: ${props.effect}`);
    }
    
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
    
    if (props.controller && !moduleCache.Controller) {
      const { Controller } = await import('swiper/modules');
      moduleCache.Controller = markRaw(Controller);
    }
    if (props.controller) modules.push(moduleCache.Controller);
    
    // Effect 모듈들 - 전역 등록된 모듈 사용
    if (props.effect === 'fade') {
      if (!moduleCache.EffectFade) {
        moduleCache.EffectFade = markRaw(EffectFade);
        if (props.debug) console.log('[getRequiredModules] EffectFade from global registry:', EffectFade);
      }
      modules.push(moduleCache.EffectFade);
      if (props.debug) console.log('[getRequiredModules] EffectFade added to modules');
    }
    
    if (props.effect === 'cube') {
      if (!moduleCache.EffectCube) {
        moduleCache.EffectCube = markRaw(EffectCube);
        if (props.debug) console.log('[getRequiredModules] EffectCube from global registry:', EffectCube);
      }
      modules.push(moduleCache.EffectCube);
      if (props.debug) console.log('[getRequiredModules] EffectCube added to modules');
    }
    
    if (props.effect === 'coverflow' || props.effect === 'cylinder') {
      if (!moduleCache.EffectCoverflow) {
        moduleCache.EffectCoverflow = markRaw(EffectCoverflow);
        if (props.debug) console.log('[getRequiredModules] EffectCoverflow from global registry:', EffectCoverflow);
      }
      modules.push(moduleCache.EffectCoverflow);
      if (props.debug) console.log(`[getRequiredModules] EffectCoverflow added to modules for ${props.effect}`);
    }
    
    if (props.effect === 'flip') {
      if (!moduleCache.EffectFlip) {
        moduleCache.EffectFlip = markRaw(EffectFlip);
        if (props.debug) console.log('[getRequiredModules] EffectFlip from global registry:', EffectFlip);
      }
      modules.push(moduleCache.EffectFlip);
      if (props.debug) console.log('[getRequiredModules] EffectFlip added to modules');
    }
    
     if (props.effect === 'cards') {
       if (!moduleCache.EffectCards) {
         moduleCache.EffectCards = markRaw(EffectCards);
         if (props.debug) console.log('[getRequiredModules] EffectCards from global registry:', EffectCards);
       }
       modules.push(moduleCache.EffectCards);
       if (props.debug) console.log('[getRequiredModules] EffectCards added to modules');
     }
    
    if (props.effect === 'creative') {
      if (!moduleCache.EffectCreative) {
        moduleCache.EffectCreative = markRaw(EffectCreative);
        if (props.debug) console.log('[getRequiredModules] EffectCreative from global registry:', EffectCreative);
      }
      modules.push(moduleCache.EffectCreative);
      if (props.debug) console.log('[getRequiredModules] EffectCreative added to modules');
    }
    
    if (props.debug) {
      console.log(`[getRequiredModules] Final modules:`, modules.map(m => m.name || 'Unknown'));
      console.log(`[getRequiredModules] Note: All effect modules are globally registered via SwiperCore.use()`);
    }
    
    return markRaw(modules);
  };
  
  // ============================================================================
  // COMPONENT LOGIC
  // ============================================================================
  
  // Props 정의 - variants 기본값 포함
  const props = withDefaults(defineProps<SCSwiperProps>(), {
    slides: () => [],
    pagination: true,
    paginationType: undefined,
    navigation: true,
    scrollbar: false,
    autoplay: false,
    controller: false,
    controllerGroup: undefined,
    loop: false,
    slidesPerView: 1,
    spaceBetween: 0,
    centeredSlides: false,
    direction: 'horizontal',
    speed: 300,
    effect: 'slide',
    swiperId: undefined,
    debug: true,
    exposeInstance: false,
    // Variants 기본값
    size: 'medium',
    theme: 'default',
    navigationStyle: 'default',
    paginationStyle: 'default',
    state: 'normal',
    spacing: 'normal'
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
    slideClick: [{ slide: any; index: number; slideData: any; swiper: any }];
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
  
  // 디버그용 슬라이드 변경 카운터
  const slideChangeCount = ref(0);
  
  // 간단한 ID 시스템 (Controller 기반으로 단순화)
  const swiperId = computed(() =>
    props.swiperId || generateUniqueId('swiper')
  );

  // Variants 클래스 계산
  const containerClasses = computed(() => {
    return SwiperVariants({
      size: props.size,
      theme: props.theme,
      effect: props.effect,
      direction: props.direction,
      navigationStyle: props.navigationStyle,
      paginationStyle: props.paginationStyle,
      state: props.state,
      spacing: props.spacing,
    });
  });
  
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
      `.swiper-pagination-${swiperId.value}`
    )
  );
  
  const navigationConfig = computed(() => 
    createNavigationConfig(props.navigation, swiperId.value)
  );
  
  const scrollbarConfig = computed(() => 
    createScrollbarConfig(props.scrollbar, swiperId.value)
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
  
  // Controller 설정 (새로 추가)
  const controllerConfig = computed(() => 
    createControllerConfig(
      props.controller || false,
      props.controllerGroup,
      swiperInstance.value
    )
  );
  
  // Effect별 추가 설정 - TypeScript 타입 지정으로 인덱스 접근 오류 해결
  const effectConfig = computed((): EffectSettings => {
    switch (props.effect) {
      case 'cube':
        return {
          cubeEffect: {
            shadow: true,
            slideShadows: true,
            shadowOffset: 50,
            shadowScale: 0.94,
          }
        };
        case 'coverflow':
          return {
            coverflowEffect: {
              rotate: 0,
              stretch: 0,
              depth: 100,
              modifier: 1,
              slideShadows: true,
            }
          };
        //   rotate: 120,       // 극대화된 회전각
        //       stretch: -100,     // 최대 겹침 효과
        //       depth: 800,        // 최대 깊이
        //       modifier: 5,       // 최대 효과 강도
        //       slideShadows: true,
        //       scale: 0.6,        // 더 작은 비활성 슬라이드
        //       rotate: 50,
        case 'cylinder':
          return {
            coverflowEffect: {
                rotate: 120,      // forceCylinderEffect와 동일한 값으로 강화
                stretch: -100,    // 겹침 효과 극대화
                depth: 800,       // 깊이 감 강화  
                modifier: 5,      // 효과 강도 극대화
                slideShadows: true,
                scale: 0.6,       // 비활성 슬라이드 크기 조정
            }
          };
      case 'flip':
        return {
          flipEffect: {
            slideShadows: true,
            limitRotation: true,
          }
        };
      case 'fade':
        return {
          fadeEffect: {
            crossFade: true
          }
        };
       case 'cards':
         return {
           cardsEffect: {
             slideShadows: true,
             perSlideOffset: 8,
             perSlideRotate: 2,
             rotate: true,
           }
         };
      case 'creative':
        return {
          creativeEffect: {
            prev: {
              shadow: true,
              translate: ['-120%', 0, -500],
              rotate: [0, 0, -90],
            },
            next: {
              shadow: true,
              translate: ['120%', 0, -500],
              rotate: [0, 0, 90],
            },
          }
        };
      default:
        return {};
    }
  });

  // Effect에 따른 slidesPerView 조정
  const adjustedSlidesPerView = computed(() => {
    // Cube, Fade, Flip, Cards, Creative effect는 slidesPerView가 1이어야 함
    if (['cube', 'fade', 'flip', 'cards', 'creative'].includes(props.effect || '')) {
      return 1;
    }
      // Cylinder effect는 3개가 보이도록 설정 - 주석 해제하여 적용
      if (props.effect === 'cylinder') {
        return 3;
      }
    return props.slidesPerView;
  });

  // 최종 Swiper 설정 (Effect 설정 포함)
  const swiperConfig = computed(() => {
    const baseConfig: any = {
    modules: modules.value,
    pagination: paginationConfig.value,
    navigation: navigationConfig.value,
    scrollbar: scrollbarConfig.value,
    autoplay: autoplayConfig.value,
    loop: props.loop,
      slidesPerView: adjustedSlidesPerView.value,
      spaceBetween: props.effect === 'cylinder' ? 0 : props.spaceBetween,
      centeredSlides: props.effect === 'cylinder' ? true : props.centeredSlides,
    direction: props.direction,
    speed: props.speed,
      effect: props.effect === 'cylinder' ? 'coverflow' : props.effect,
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
    };

    // Effect별 설정 추가 - TypeScript 타입 안전성을 위한 명시적 타입 캐스팅
    const effectSettings = effectConfig.value;
    Object.keys(effectSettings).forEach(key => {
      // 동적 속성 접근을 위한 타입 단언 사용
      (baseConfig as any)[key] = (effectSettings as any)[key];
    });

    // Controller가 활성화된 경우만 추가
    if (controllerConfig.value !== false) {
      baseConfig.controller = controllerConfig.value;
    }

    if (props.debug) {
      console.log(`[swiperConfig] Effect: ${props.effect}, Config:`, baseConfig);
    }

    return baseConfig;
  });
  
  // DOM 요소 연결을 위한 재시도 함수
  const connectElements = async (swiper: any, retryCount = 0) => {
    const maxRetries = 5;
    const retryDelay = 100;
    
    if (retryCount >= maxRetries) {
      console.warn(`[SCSwiper ${swiperId.value}] Max retries reached for DOM element connection`);
      return;
    }
    
    // Navigation 버튼 연결
    if (props.navigation !== false) {
      const nextEl = document.querySelector(`.swiper-button-next-${swiperId.value}`);
      const prevEl = document.querySelector(`.swiper-button-prev-${swiperId.value}`);
      
      if (nextEl && prevEl) {
        // Swiper Navigation 모듈 연결
        if (swiper.navigation) {
          // 기존 navigation 제거
          if (swiper.navigation.nextEl || swiper.navigation.prevEl) {
            swiper.navigation.destroy();
          }
          
          swiper.navigation.nextEl = nextEl;
          swiper.navigation.prevEl = prevEl;
          swiper.navigation.init();
          swiper.navigation.update();
          
          if (props.debug) {
            console.log(`[SCSwiper ${swiperId.value}] Navigation module reinitialized`);
          }
        }
        
        // 직접 DOM 이벤트 리스너는 제거 - Swiper 내장 navigation 사용
        
        if (props.debug) {
          console.log(`[SCSwiper ${swiperId.value}] Navigation connected successfully on attempt ${retryCount + 1}`);
          console.log(`[SCSwiper ${swiperId.value}] Using Swiper's built-in navigation (no direct listeners)`);
        }
      } else {
        if (props.debug) {
          console.log(`[SCSwiper ${swiperId.value}] Navigation elements not ready, retrying... (${retryCount + 1}/${maxRetries})`);
        }
        setTimeout(() => connectElements(swiper, retryCount + 1), retryDelay);
        return;
      }
    }
    
    // Pagination 연결
    if (props.pagination !== false) {
      const paginationEl = document.querySelector(`.swiper-pagination-${swiperId.value}`);
      
      if (paginationEl) {
        // Swiper Pagination 모듈 연결 (강화된 버전)
        if (swiper.pagination) {
          // 1. 기존 pagination 제거
          if (swiper.pagination.el) {
            swiper.pagination.destroy();
          }
          
          // 2. 새로운 element 설정
          swiper.pagination.el = paginationEl;
          
          // 3. 재초기화
          swiper.pagination.init();
          swiper.pagination.render();
          swiper.pagination.update();
          
          if (props.debug) {
            console.log(`[SCSwiper ${swiperId.value}] Pagination module reconnected`);
          }
        } else {
          // pagination 모듈이 없는 경우 수동으로 설정
          if (props.debug) {
            console.log(`[SCSwiper ${swiperId.value}] No pagination module, manual setup required`);
          }
        }
        
        // pagination type별 특별 처리
        if (props.paginationType === 'fraction') {
          const updatePagination = () => {
            const current = swiper.realIndex + 1;
            const total = swiper.slides.length;
            paginationEl.innerHTML = `${current} / ${total}`;
          };
          
          // 초기 설정
          updatePagination();
          
          // 슬라이드 변경 시 업데이트
          swiper.on('slideChange', updatePagination);
        } else if (props.paginationType === 'bullets') {
          // bullets 타입의 경우 수동으로 bullets 생성
          const renderBullets = () => {
            const total = swiper.slides.length;
            const current = swiper.activeIndex;
            
            if (props.debug) {
              console.log(`[${swiperId.value}] Bullets render - activeIndex: ${current}, realIndex: ${swiper.realIndex}, total: ${total}`);
            }
            
            let bulletsHTML = '';
            for (let i = 0; i < total; i++) {
              const activeClass = i === current ? ' swiper-pagination-bullet-active' : '';
              bulletsHTML += `<span class="swiper-pagination-bullet${activeClass}" data-index="${i}"></span>`;
            }
            paginationEl.innerHTML = bulletsHTML;
            
            // bullet 클릭 이벤트 추가 (이벤트 중복 방지)
            paginationEl.querySelectorAll('.swiper-pagination-bullet').forEach((bullet, index) => {
              // 기존 리스너 제거
              bullet.replaceWith(bullet.cloneNode(true));
            });
            
            paginationEl.querySelectorAll('.swiper-pagination-bullet').forEach((bullet, index) => {
              bullet.addEventListener('click', (e) => {
                e.preventDefault();
                e.stopPropagation();
                
                if (props.debug) {
                  console.log(`[${swiperId.value}] Bullet clicked: ${index}, current: ${swiper.activeIndex}`);
                }
                
                // 이미 같은 슬라이드면 무시
                if (index === swiper.activeIndex) {
                  return;
                }
                
                // loop이 비활성화된 경우 직접 이동
                if (!props.loop) {
                  swiper.slideTo(index, 300);
                } else {
                  // loop이 활성화된 경우 실제 슬라이드로 이동
                  swiper.slideToLoop(index, 300);
                }
              });
            });
          };
          
          // 초기 렌더링
          renderBullets();
          
          // 슬라이드 변경 시 업데이트
          swiper.on('slideChange', renderBullets);
          
          if (props.debug) {
            console.log(`[SCSwiper ${swiperId.value}] Manual bullets rendered`);
          }
        } else if (props.paginationType === 'progressbar') {
          // progressbar 타입 수동 구현
          const renderProgressbar = () => {
            const progress = (swiper.realIndex + 1) / swiper.slides.length * 100;
            paginationEl.innerHTML = `
              <span class="swiper-pagination-progressbar-fill" style="transform: translateX(${progress - 100}%);"></span>
            `;
          };
          
          // 초기 렌더링
          renderProgressbar();
          
          // 슬라이드 변경 시 업데이트
          swiper.on('slideChange', renderProgressbar);
          
          if (props.debug) {
            console.log(`[SCSwiper ${swiperId.value}] Manual progressbar rendered`);
          }
        } else if (props.paginationType === 'custom') {
          // custom 타입의 경우 사용자 정의 렌더링
          const renderCustomPagination = () => {
            const current = swiper.realIndex + 1;
            const total = swiper.slides.length;
            paginationEl.innerHTML = `
              <div style="display: flex; gap: 8px; align-items: center;">
                <span style="background: #007aff; color: white; padding: 4px 8px; border-radius: 12px; font-size: 12px;">
                  ${current}
                </span>
                <span style="color: #666; font-size: 12px;">of</span>
                <span style="background: #e0e0e0; color: #333; padding: 4px 8px; border-radius: 12px; font-size: 12px;">
                  ${total}
                </span>
              </div>
            `;
          };
          
          // 초기 설정
          renderCustomPagination();
          
          // 슬라이드 변경 시 업데이트
          swiper.on('slideChange', renderCustomPagination);
        }
        
        if (props.debug) {
          console.log(`[SCSwiper ${swiperId.value}] Pagination connected successfully on attempt ${retryCount + 1}`);
          console.log(`[SCSwiper ${swiperId.value}] Pagination type: ${props.paginationType}`);
          console.log(`[SCSwiper ${swiperId.value}] Pagination config:`, swiper.params.pagination);
          console.log(`[SCSwiper ${swiperId.value}] Pagination element innerHTML:`, paginationEl.innerHTML);
          console.log(`[SCSwiper ${swiperId.value}] Effect: ${props.effect}`);
          console.log(`[SCSwiper ${swiperId.value}] Effect config:`, swiper.params.effect);
          console.log(`[SCSwiper ${swiperId.value}] Loaded modules:`, modules.value.map(m => m.name || 'Unknown'));
          console.log(`[SCSwiper ${swiperId.value}] Effect object:`, swiper[props.effect || '']);
          console.log(`[SCSwiper ${swiperId.value}] SlidesPerView:`, swiper.params.slidesPerView);
          console.log(`[SCSwiper ${swiperId.value}] Wrapper classes:`, swiper.wrapperEl?.className);
        }
      } else {
        if (props.debug) {
          console.log(`[SCSwiper ${swiperId.value}] Pagination element not ready, retrying... (${retryCount + 1}/${maxRetries})`);
        }
        setTimeout(() => connectElements(swiper, retryCount + 1), retryDelay);
        return;
      }
    }
  };
  
  // 이벤트 핸들러들
  const onSwiperInit = async (swiper: any) => {
    swiperInstance.value = swiper;
    totalSlides.value = swiper.slides.length;
    
    if (props.debug) {
      console.log(`[SCSwiper ${swiperId.value}] Swiper initialized, connecting elements...`);
      console.log(`[SCSwiper ${swiperId.value}] Initial effect state:`, swiper.params.effect);
      console.log(`[SCSwiper ${swiperId.value}] Expected effect:`, props.effect);
    }
    
    // Effect가 올바르게 적용되지 않은 경우 강제 설정
    if (swiper.params.effect !== props.effect && props.effect && props.effect !== 'slide') {
      if (props.debug) {
        console.log(`[SCSwiper ${swiperId.value}] Force setting effect to: ${props.effect}`);
      }
      
      // Effect 파라미터 강제 설정
      swiper.params.effect = props.effect;
      
      // Effect별 설정도 강제 적용 - TypeScript 인덱스 시그니처 오류 해결
      const effectSettings = effectConfig.value;
      Object.keys(effectSettings).forEach(key => {
        // 동적 속성 접근을 위한 타입 단언: swiper.params와 effectSettings 모두 any로 캐스팅
        (swiper.params as any)[key] = (effectSettings as any)[key];
      });
      // Swiper 업데이트
      swiper.update();
      swiper.updateSize();
      swiper.updateSlides();
      
      if (props.debug) {
        console.log(`[SCSwiper ${swiperId.value}] Effect updated to:`, swiper.params.effect);
        console.log(`[SCSwiper ${swiperId.value}] Effect settings:`, effectSettings);
      }
    }
    
    // Cylinder effect 전용 강제 설정
    if (props.effect === 'cylinder') {
      const forceCylinderEffect = () => {
        // Swiper에 coverflow 클래스 강제 추가
        if (swiper.el) {
          swiper.el.classList.add('swiper-coverflow');
        }
        
        // Coverflow 효과 파라미터 강제 재설정
        swiper.params.effect = 'coverflow';
        swiper.params.coverflowEffect = {
          rotate: 120,
          stretch: -100,
          depth: 800,
          modifier: 5,
          slideShadows: true,
          scale: 0.6,
        };
        
        // 강제 업데이트
        swiper.update();
        swiper.updateSize();
        swiper.updateSlides();
        
        const slides = swiper.slides;
        if (props.debug) {
          console.log(`[SCSwiper ${swiperId.value}] Cylinder effect - Force applied`);
          console.log(`[SCSwiper ${swiperId.value}] Effect:`, swiper.params.effect);
          console.log(`[SCSwiper ${swiperId.value}] Coverflow params:`, swiper.params.coverflowEffect);
          console.log(`[SCSwiper ${swiperId.value}] Swiper classes:`, swiper.el?.className);
          // TypeScript 매개변수 타입 지정으로 암시적 any 타입 오류 해결
          slides.forEach((slide: HTMLElement, index: number) => {
            console.log(`  Slide ${index}:`, slide.className);
          });
        }
      };
      
      // 초기 강제 설정
      setTimeout(forceCylinderEffect, 100);
      
      // 슬라이드 변경 시마다 강제 설정 확인
      swiper.on('slideChange', forceCylinderEffect);
    }

    // DOM 요소 연결 (재시도 로직 포함)
    await nextTick();
    connectElements(swiper);
    
    // Controller 그룹에 추가
    if (props.controllerGroup) {
      const groupInstances = controllerGroups.get(props.controllerGroup) || [];
      groupInstances.push(swiper);
      controllerGroups.set(props.controllerGroup, groupInstances);
      
      // 기존 그룹 멤버들과 상호 연결
      groupInstances.forEach(instance => {
        if (instance !== swiper && instance.controller) {
          const otherInstances = groupInstances.filter(i => i !== instance);
          instance.controller.control = otherInstances;
        }
      });
    }
    
    emit('init', swiper);
    
    if (props.debug) {
      console.log(`[SCSwiper ${swiperId.value}] Initialized with ${totalSlides.value} slides`);
      console.log(`[SCSwiper ${swiperId.value}] Loaded modules:`, modules.value);
      console.log(`[SCSwiper ${swiperId.value}] Navigation config:`, navigationConfig.value);
      console.log(`[SCSwiper ${swiperId.value}] Pagination config:`, paginationConfig.value);
      if (props.controllerGroup) {
        console.log(`[SCSwiper ${swiperId.value}] Added to controller group: ${props.controllerGroup}`);
      }
    }
  };
  
  const onSlideChange = (swiper: any) => {
    const previousIndex = currentSlideIndex.value;
    currentSlideIndex.value = swiper.activeIndex;
    isAtStart.value = swiper.isBeginning;
    isAtEnd.value = swiper.isEnd;
    
    // 디버그 카운터 증가
    slideChangeCount.value++;
    
    if (props.debug) {
      console.log(`[${swiperId.value}] Slide change #${slideChangeCount.value}: ${previousIndex} → ${swiper.activeIndex} (realIndex: ${swiper.realIndex})`);
      console.log(`[${swiperId.value}] Navigation elements: next=${swiper.navigation?.nextEl ? 'connected' : 'none'}, prev=${swiper.navigation?.prevEl ? 'connected' : 'none'}`);
    }
    
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

  const onSlideClick = (swiper: any, event: Event) => {
    const clickedSlide = event.target as HTMLElement;
    const slideElement = clickedSlide.closest('.swiper-slide') as HTMLElement;
    
    if (slideElement) {
      // 슬라이드 인덱스 찾기
      const slides = Array.from(swiper.slides);
      const clickedIndex = slides.indexOf(slideElement);
      
      // 슬라이드 데이터 찾기
      let slideData = null;
      if (props.slides && props.slides[clickedIndex]) {
        slideData = props.slides[clickedIndex];
      }
      
      if (props.debug) {
        console.log(`[${swiperId.value}] Slide clicked:`, {
          index: clickedIndex,
          activeIndex: swiper.activeIndex,
          realIndex: swiper.realIndex,
          slideData,
          slideElement
        });
      }
      
      // 이벤트 발생
      emit('slideClick', {
        slide: slideElement,
        index: clickedIndex,
        slideData,
        swiper
      });
    }
  };
  
  const onProgress = (swiper: any, progress: number) => {
    scrollProgress.value = Math.round(progress * 100);
    emit('progress', { progress });
  };
  
  // 에러 처리
  onErrorCaptured((error) => {
    console.error(`[SCSwiper ${swiperId.value}] Error:`, error);
    emit('error', error);
    return false;
  });
  
  // 초기화
  onMounted(async () => {
    try {
      validateSwiperProps(props);
      modules.value = await getRequiredModules(props);
      
      // DOM 요소가 완전히 렌더링될 때까지 기다림
      await nextTick();
      await nextTick(); // 추가 틱으로 안정성 확보
      
      if (props.debug) {
        console.log(`[SCSwiper ${swiperId.value}] Mounted with modules:`, modules.value.map(m => m.name || 'Unknown'));
        console.log(`[SCSwiper ${swiperId.value}] Effect:`, props.effect);
        console.log(`[SCSwiper ${swiperId.value}] Effect config:`, effectConfig.value);
        console.log(`[SCSwiper ${swiperId.value}] Adjusted SlidesPerView:`, adjustedSlidesPerView.value);
        console.log(`[SCSwiper ${swiperId.value}] Final swiperConfig:`, swiperConfig.value);
        
        // DOM 요소 확인
        const nextEl = document.querySelector(`.swiper-button-next-${swiperId.value}`);
        const prevEl = document.querySelector(`.swiper-button-prev-${swiperId.value}`);
        const paginationEl = document.querySelector(`.swiper-pagination-${swiperId.value}`);
        
        console.log(`[SCSwiper ${swiperId.value}] DOM Elements at mount:`, {
          nextEl: nextEl ? 'found' : 'not found',
          prevEl: prevEl ? 'found' : 'not found',
          paginationEl: paginationEl ? 'found' : 'not found'
        });
      }
    } catch (error) {
      console.error(`[SCSwiper ${swiperId.value}] Mount error:`, error);
      emit('error', error as Error);
    }
  });
  
  // 메모리 누수 방지
  onUnmounted(() => {
    if (swiperInstance.value) {
      try {
        // Controller 그룹에서 제거
        if (props.controllerGroup) {
          const groupInstances = controllerGroups.get(props.controllerGroup) || [];
          const updatedInstances = groupInstances.filter(instance => instance !== swiperInstance.value);
          
          if (updatedInstances.length === 0) {
            controllerGroups.delete(props.controllerGroup);
          } else {
            controllerGroups.set(props.controllerGroup, updatedInstances);
            // 남은 인스턴스들 간의 연결 재설정
            updatedInstances.forEach(instance => {
              if (instance.controller) {
                const otherInstances = updatedInstances.filter(i => i !== instance);
                instance.controller.control = otherInstances;
              }
            });
          }
        }
        
        swiperInstance.value.destroy(true, true);
        if (props.debug) {
          console.log(`[SCSwiper ${swiperId.value}] Destroyed`);
        }
      } catch (error) {
        console.error(`[SCSwiper ${swiperId.value}] Destroy error:`, error);
      }
    }
  });
  
  // Props 변경 감지 (성능 최적화)
  watchEffect(() => {
    if (swiperInstance.value) {
      swiperInstance.value.update();
    }
  });
  
  // 디버그용 네비게이션 테스트 함수
  const testNavigation = () => {
    if (swiperInstance.value) {
      console.log(`[SCSwiper ${swiperId.value}] Testing navigation...`);
      console.log('Current slide:', swiperInstance.value.activeIndex);
      console.log('Total slides:', swiperInstance.value.slides.length);
      console.log('Navigation object:', swiperInstance.value.navigation);
      
      // 수동으로 다음 슬라이드로 이동
      swiperInstance.value.slideNext();
    }
  };
  
  // 외부에서 접근 가능한 메서드들
  defineExpose({
    swiper: swiperInstance,
    slideTo: (index: number) => swiperInstance.value?.slideTo(index),
    slideNext: () => swiperInstance.value?.slideNext(),
    slidePrev: () => swiperInstance.value?.slidePrev(),
    update: () => swiperInstance.value?.update(),
    testNavigation, // 디버그용 함수 추가
    
    // 추가: 외부 컴포넌트에서 필요한 상태들
    currentSlideIndex: computed(() => currentSlideIndex.value),
    totalSlides: computed(() => totalSlides.value),
    isAtStart: computed(() => isAtStart.value),
    isAtEnd: computed(() => isAtEnd.value),
    uniqueId: computed(() => swiperId.value),
  });
  </script>
  
  <style scoped>
  .sc-swiper-container {
    position: relative;
    width: 100%;
    min-height: 300px; /* 최소 높이 설정 */
  }

  .sc-swiper-container .swiper {
    width: 100%;
    height: 100%;
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
  
  /* Pagination */
  :deep(.swiper-pagination) {
    display: block !important;
    bottom: 10px !important;
    z-index: 10 !important;
    opacity: 1 !important;
    visibility: visible !important;
  }
  
  :deep(.swiper-pagination-bullet) {
    width: 12px !important;
    height: 12px !important;
    background: rgba(0, 0, 0, 0.3) !important;
    opacity: 1 !important;
    margin: 0 4px !important;
    transition: all 0.3s ease !important;
  }
  
  :deep(.swiper-pagination-bullet-active) {
    background: #007aff !important;
    transform: scale(1.2) !important;
  }
  
  /* Pagination fraction */
  :deep(.swiper-pagination-fraction) {
    display: block !important;
    position: absolute !important;
    bottom: 10px !important;
    background: rgba(0, 0, 0, 0.7) !important;
    color: white !important;
    padding: 6px 12px !important;
    border-radius: 12px !important;
    font-size: 14px !important;
    font-weight: 500 !important;
    width: auto !important;
    left: 50% !important;
    transform: translateX(-50%) !important;
    z-index: 10 !important;
    opacity: 1 !important;
    visibility: visible !important;
    text-align: center !important;
  }
  
  /* Pagination progressbar */
  :deep(.swiper-pagination-progressbar),
  :deep(.swiper-pagination[data-type="progressbar"]) {
    position: relative !important;
    background: rgba(0, 0, 0, 0.1) !important;
    height: 4px !important;
    border-radius: 2px !important;
    overflow: hidden !important;
  }

  :deep(.swiper-pagination-progressbar-fill) {
    position: absolute !important;
    left: 0 !important;
    top: 0 !important;
    width: 100% !important;
    height: 100% !important;
    background: #007aff !important;
    border-radius: 2px !important;
    transform-origin: left center !important;
    transition: transform 0.3s ease !important;
  }
  
  /* Scrollbar */
  :deep(.swiper-scrollbar) {
    background: rgba(0, 0, 0, 0.1) !important;
    border-radius: 4px !important;
  }
  
  :deep(.swiper-scrollbar-drag) {
    background: #007aff !important;
    border-radius: 4px !important;
  }
  
  /* Cards Effect 전용 스타일 */
  :deep(.swiper-cards) .swiper-slide {
    border-radius: 18px !important;
    box-shadow: 0 15px 50px rgba(0, 0, 0, 0.2) !important;
    background: linear-gradient(45deg, #667eea 0%, #764ba2 100%) !important;
    overflow: hidden !important;
  }

  :deep(.swiper-cards) .swiper-slide.swiper-slide-active {
    z-index: 10 !important;
    transform: scale(1.02) !important;
  }

  :deep(.swiper-cards) .swiper-slide-shadow-cards {
    background: rgba(0, 0, 0, 0.3) !important;
  }

  /* Creative Effect 전용 스타일 */
  :deep(.swiper-creative) .swiper-slide {
    border-radius: 12px !important;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15) !important;
    background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%) !important;
    overflow: hidden !important;
  }

  :deep(.swiper-creative) .swiper-slide.swiper-slide-shadow-creative {
    background: rgba(0, 0, 0, 0.2) !important;
  }

  /* Cylinder Effect 컨테이너 설정 - 강화된 3D */
  :deep(.sc-swiper-container[data-effect="cylinder"]) {
    perspective: 2000px !important;
    perspective-origin: center center !important;
    overflow: visible !important;
    min-height: 400px !important;
    padding: 50px 0 !important;
    margin: 30px 0 !important;
  }

  :deep(.sc-swiper-container[data-effect="cylinder"]) .swiper {
    overflow: visible !important;
    height: 100% !important;
  }

  :deep(.sc-swiper-container[data-effect="cylinder"]) .swiper-coverflow {
    transform-style: preserve-3d !important;
    overflow: visible !important;
    height: 100% !important;
  }

  :deep(.sc-swiper-container[data-effect="cylinder"]) .swiper-wrapper {
    transform-style: preserve-3d !important;
    overflow: visible !important;
    height: 100% !important;
    display: flex !important;
    align-items: center !important;
  }

  /* Swiper가 coverflow 클래스를 갖도록 강제 - CSS 우선순위 강화 */
  :deep(.sc-swiper-container[data-effect="cylinder"]) .swiper,
  :deep(.sc-swiper-container[data-effect="cylinder"]) .swiper.swiper-coverflow {
    /* Swiper coverflow 효과가 확실히 적용되도록 구체적 선택자 사용 */
    transform-style: preserve-3d !important;
    overflow: visible !important;
  }

  :deep(.sc-swiper-container[data-effect="cylinder"]) .swiper.swiper-coverflow .swiper-wrapper,
  :deep(.sc-swiper-container[data-effect="cylinder"]) .swiper-wrapper {
    transform-style: preserve-3d !important;
    overflow: visible !important;
  }

  /* Cylinder Effect 전용 스타일 - 다중 data-v 속성 문제 해결을 위한 글로벌 스타일 */
  
  /* 중복된 cylinder 스타일들을 글로벌 스타일로 이동함 */
  
  /* 반응형 */
  @media (max-width: 768px) {
    :deep(.swiper-button-next),
    :deep(.swiper-button-prev) {
      width: 36px !important;
      height: 36px !important;
      margin-top: -18px !important;
    }
    
    :deep(.swiper-button-next::after),
    :deep(.swiper-button-prev::after) {
      font-size: 14px !important;
    }
    
    /* 모바일에서 Cards 효과 조정 */
    :deep(.swiper-cards) .swiper-slide {
      border-radius: 12px !important;
    }
    
    /* 모바일에서 Cylinder 효과 조정 - 강화된 3D */
    :deep(.sc-swiper-container[data-effect="cylinder"]) {
      perspective: 1500px !important;
    }

    /* :deep(.sc-swiper-container[data-effect="cylinder"]) .swiper-coverflow .swiper-slide {
      transform-style: preserve-3d !important;
      transition: all 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94) !important;
      border-radius: 10px !important;
      box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1) !important;

      &.swiper-slide-next ,
      &.swiper-slide-prev {
        transform: scale(1);
        opacity: 1;
        z-index: 2;
      }

    }
     */
    /* 모바일 cylinder 스타일들도 글로벌 스타일로 이동함 */
  }
  /* 슬라이드 클릭 가능 커서 스타일 */
:deep(.swiper-slide) {
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

:deep(.swiper-slide:hover) {
  transform: scale(1.02);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

:deep(.swiper-slide:active) {
  transform: scale(0.98);
}
  </style>

<!-- 다중 data-v 속성 문제 해결을 위한 글로벌 Cylinder Effect 스타일 -->
<style>
/* Cylinder Effect 전용 스타일 - 글로벌 스타일로 다중 data-v 속성 문제 해결 */
.sc-swiper-container[data-effect="cylinder"] .swiper-slide,
.sc-swiper-container[data-effect="cylinder"] .swiper-coverflow .swiper-slide {
  border-radius: 15px !important;
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.4) !important;
  /* background: linear-gradient(45deg, #a8edea 0%, #fed6e3 100%) !important; */
  overflow: visible !important;
  transform-style: preserve-3d !important;
  transition: all 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94) !important;
  backface-visibility: visible !important;
  will-change: transform !important;

  padding-top: 50px !important;
  
  /* 기본 cylinder 스타일 강제 적용 */
  transform: scale(0.8) translateZ(-200px) rotateY(45deg) !important;
}

/* 가운데 슬라이드 (active) - 앞으로 나오고 위로 올라감 */
.sc-swiper-container[data-effect="cylinder"] .swiper-coverflow .swiper-slide.swiper-slide-active,
.sc-swiper-container[data-effect="cylinder"] .swiper-slide.swiper-slide-active {
  z-index: 20 !important;
  transform: scale(1.2) translateY(-30px) translateZ(100px) rotateY(0deg) !important;
  box-shadow: 0 40px 80px rgba(0, 0, 0, 0.5) !important;
  border: 4px solid rgba(255, 255, 255, 0.4) !important;
  filter: brightness(1.1) contrast(1.1) !important;
}

/* 이전 슬라이드 - 왼쪽으로 극적 회전하며 뒤로 */
.sc-swiper-container[data-effect="cylinder"] .swiper.swiper-coverflow .swiper-wrapper .swiper-slide.swiper-slide-prev,
.sc-swiper-container[data-effect="cylinder"] .swiper-coverflow .swiper-slide.swiper-slide-prev,
.sc-swiper-container[data-effect="cylinder"] .swiper-slide.swiper-slide-prev {
  z-index: 5 !important;
  transform: scale(0.65) translateY(40px) translateZ(-150px) rotateY(85deg) translateX(-30px) !important;
  opacity: 0.4 !important;
  filter: brightness(0.5) contrast(0.8) !important;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.6) !important;
  transition: all 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94) !important;
  transform-origin: center center !important;
}

/* 다음 슬라이드 - 오른쪽으로 극적 회전하며 뒤로 */
.sc-swiper-container[data-effect="cylinder"] .swiper.swiper-coverflow .swiper-wrapper .swiper-slide.swiper-slide-next,
.sc-swiper-container[data-effect="cylinder"] .swiper-coverflow .swiper-slide.swiper-slide-next,
.sc-swiper-container[data-effect="cylinder"] .swiper-slide.swiper-slide-next {
  z-index: 5 !important;
  transform: scale(0.65) translateY(40px) translateZ(-150px) rotateY(-85deg) translateX(30px) !important;
  opacity: 0.4 !important;
  filter: brightness(0.5) contrast(0.8) !important;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.6) !important;
  transition: all 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94) !important;
  transform-origin: center center !important;
}

/* 양쪽 슬라이드들 기본 스타일 (prev/next가 아닌 경우) */
.sc-swiper-container[data-effect="cylinder"] .swiper-coverflow .swiper-slide:not(.swiper-slide-active):not(.swiper-slide-prev):not(.swiper-slide-next),
.sc-swiper-container[data-effect="cylinder"] .swiper-slide:not(.swiper-slide-active):not(.swiper-slide-prev):not(.swiper-slide-next) {
  z-index: 5 !important;
  transform: scale(0.85) translateY(15px) translateZ(-30px) !important;
  opacity: 0.7 !important;
  filter: brightness(0.8) !important;
}

/* 그림자 효과 개선 */
.sc-swiper-container[data-effect="cylinder"] .swiper-coverflow .swiper-slide-shadow-coverflow {
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.2), rgba(0, 0, 0, 0.6)) !important;
  border-radius: 15px !important;
}

/* 모바일 반응형 cylinder 스타일 */
@media (max-width: 768px) {
  .sc-swiper-container[data-effect="cylinder"] .swiper-coverflow .swiper-slide.swiper-slide-active,
  .sc-swiper-container[data-effect="cylinder"] .swiper-slide.swiper-slide-active {
    transform: scale(1.15) translateY(-25px) translateZ(80px) !important;
  }
  
  /* 모바일에서 이전 슬라이드 - 강화된 회전 */
  .sc-swiper-container[data-effect="cylinder"] .swiper-coverflow .swiper-slide.swiper-slide-prev,
  .sc-swiper-container[data-effect="cylinder"] .swiper-slide.swiper-slide-prev {
    transform: scale(0.6) translateY(30px) translateZ(-120px) rotateY(75deg) translateX(-20px) !important;
    opacity: 0.3 !important;
  }
  
  /* 모바일에서 다음 슬라이드 - 강화된 회전 */
  .sc-swiper-container[data-effect="cylinder"] .swiper-coverflow .swiper-slide.swiper-slide-next,
  .sc-swiper-container[data-effect="cylinder"] .swiper-slide.swiper-slide-next {
    transform: scale(0.6) translateY(30px) translateZ(-120px) rotateY(-75deg) translateX(20px) !important;
    opacity: 0.3 !important;
  }
  
  /* 모바일에서 기본 양쪽 슬라이드 */
  .sc-swiper-container[data-effect="cylinder"] .swiper-coverflow .swiper-slide:not(.swiper-slide-active):not(.swiper-slide-prev):not(.swiper-slide-next),
  .sc-swiper-container[data-effect="cylinder"] .swiper-slide:not(.swiper-slide-active):not(.swiper-slide-prev):not(.swiper-slide-next) {
    transform: scale(0.8) translateY(20px) translateZ(-80px) !important;
  }
}
</style>
