
<template>
  <div class="demo-title">유형 : [module] ScDataList - Basic with Title</div>
  <section class="section">
    <ScDataList :data="scBasicData">
      <template #item-2-prepend> prepend slot </template>
      <template #item-2-append> append slot </template>
      <template #item-4-append>
        <div class="data-list__btn-wrap">
          <BoxButton
            color="tertiary"
            text="메모 입력하기"
          />
        </div>
      </template>
    </ScDataList>
    <Divider
      variant="basic"
      color="tertiary"
      class="my-6"
    />
  </section>

  <div class="demo-title">유형 : [module] Expandable DataList (통합)</div>
  <!-- S : 확장형 DataList -->
  <ScDataList
    :data="expandableData"
    class="mt-3"
  />
  <!-- E : 확장형 DataList -->

  <div class="demo-title">유형 1 : DataList 기본</div>
  <!-- S : 유형 1 : DataList 기본 -->
  <ScDataList :data="basicData" />
  <!-- E : 유형 1 : DataList 기본 -->

  <div class="demo-title">유형 2 : DataList - Box</div>
  <!-- S : 유형 2 : DataList - box -->
  <ScDataList :data="boxData" />
  <!-- E : 유형 2 : DataList - box -->

  <div class="demo-title">유형 3 : DataList - Box in Box</div>
  <!-- S : 유형 3 : DataList - box in box -->
  <ScDataList :data="boxInBoxData">
    <template #actions>
      <BoxButtonGroup variant="100">
        <BoxButton
          color="secondary"
          size="medium"
          text="텍스트"
        />
      </BoxButtonGroup>
    </template>
  </ScDataList>
  <!-- E : 유형 3 : DataList - box in box -->

  <div class="demo-title">유형 5 : 복합 유형 (Composite Type)</div>
  <!-- S : 유형 5 : 복합 유형 -->
  <ScDataList
    :data="compositeData"
    :debug="true"
  >
    <!-- 복합 유형에서는 우선순위에 따라 Expandable로 동작 -->
    <template #expandable-header="{ expanded, toggle }">
      <div class="custom-header">
        <h3>복합 데이터 ({{ expanded ? '확장됨' : '축소됨' }})</h3>
        <button @click="toggle">토글</button>
      </div>
    </template>
  </ScDataList>
  <!-- E : 유형 5 : 복합 유형 -->

  <Divider
    variant="basic"
    color="tertiary"
    class="my-6"
  />

  <div class="demo-title">고급: 유형별 커스텀 슬롯 활용</div>
  
  <!-- Expandable with Custom Slots -->
  <div class="demo-subtitle">Expandable 슬롯</div>
  <ScDataList :data="expandableData">
    <template #expandable-header="{ expanded }">
      <div class="custom-badge"> 계좌 정보</div>
    </template>
    <template #expandable-expanded-header>
      <div class="custom-divider">▼ 추가 정보 ▼</div>
    </template>
  </ScDataList>

  <Divider variant="basic" color="tertiary" class="my-3" />

  <!-- BoxInBox with Custom Slots -->
  <div class="demo-subtitle">BoxInBox 슬롯</div>
  <ScDataList :data="boxInBoxData">
    <template #boxInBox-header>
      <div class="custom-notice"> 중요 정보</div>
    </template>
    <template #boxInBox-between>
      <Divider variant="basic" color="secondary" class="my-2" />
    </template>
    <template #boxInBox-top-footer>
      <small class="text-muted">* Gray 영역 끝</small>
    </template>
    <template #actions>
      <BoxButtonGroup variant="100">
        <BoxButton color="secondary" size="medium" text="확인" />
      </BoxButtonGroup>
    </template>
  </ScDataList>

  <Divider variant="basic" color="tertiary" class="my-3" />

  <!-- Basic with Custom Slots -->
  <div class="demo-subtitle">Basic 슬롯</div>
  <ScDataList :data="basicData">
    <template #basic-header>
      <div class="custom-title"> 고객 기본 정보</div>
    </template>
    <template #item-1-prepend>
      <span class="badge-new">NEW</span>
    </template>
    <template #basic-footer>
      <div class="custom-footer">
        <small>마지막 업데이트: 2024-01-15</small>
      </div>
    </template>
  </ScDataList>
</template>

<script setup>
import {
  BoxButton,
  BoxButtonGroup,
  Divider
} from "@shc-nss/ui/solid";
import ScDataList from "../../_module/ScDataList.vue";

// ============================================================================
// 새로운 데이터 구조 - JSON 기반
// ============================================================================

// 유형 : Basic with Title
const scBasicData = {
  title: "Data List Title",
  items: [
    { title: "출금가능금액", content: "50,000원", tooltip: "tooltip content" },
    { title: "금리", content: "연 1.00%" },
    { title: "계좌개설일", content: "2020.04.04" },
    {
      title: "자동 이체 현황",
      contentBtnText: "3건",
    },
    {
      title: "이용내역 삭제",
      tooltip: "tooltip",
      showSwitch: true,
    },
    {
      title: "내용",
      content:
        "내용이 길 경우는 줄 바꿈으로 모든 내용이 표시됩니다. 내용이 길 경우는 줄 바꿈으로 모든 내용이 표시됩니다.  내용이 길 경우는 줄 바꿈으로 모든 내용이 표시됩니다.",
    },
  ]
};

// 유형 : Expandable (확장형) - expandedItems가 있으면 자동으로 확장형으로 인식
const expandableData = {
  items: [
    { title: "출금가능금액", content: "50,000원", tooltip: "tooltip content" },
    { title: "금리", content: "연 1.00%" },
    { title: "계좌개설일", content: "2020.04.04" },
    {
      title: "자동 이체 현황",
      contentBtnText: "3건",
    },
  ],
  expandedItems: [
    { title: "통화모드", content: "KRW" },
    { title: "외화계좌 여부", content: "Y" },
    { title: "마이너스약정 여부", content: "Y" },
  ],
  expandLabel: "더보기",
  collapseLabel: "닫기"
};

// 유형 1: Basic (items만 있으면 자동으로 기본형)
const basicData = {
  items: [
    { title: "메인텍스트", tooltip: "설명 툴팁", content: "김신한", tooltipPlacement: "top-center" },
    { title: "메인텍스트", tooltip: "영문 표기", content: "KIM SHIN HAN", tooltipPlacement: "top-center" },
    { title: "메인텍스트", tooltip: "외국인 번호", content: "901231-1******", small: "(외국인 번호)", tooltipPlacement: "top-center" },
    { title: "메인텍스트", tooltip: "주소", content: "03468 서울특별시 중구 을지 1길 10, 101동 101호(을지로 1가)", tooltipPlacement: "top-center" },
    { title: "메인텍스트", tooltip: "연락처", content: "010-1234-5678", tooltipPlacement: "top-center" },
  ]
};

// 유형 2: Box (cardStyle이 있으면 자동으로 Box형)
const boxData = {
  items: [
    { title: "메인텍스트", tooltip: "국가", content: "[KR] KOREA, REPUBLIC OF", tooltipPlacement: "top-center" },
    { title: "메인텍스트", tooltip: "거주여부", content: "거주", tooltipPlacement: "top-center" },
    { title: "메인텍스트", tooltip: "성별", content: "남자", tooltipPlacement: "top-center" },
  ],
  cardStyle: {
    variant: "solid",
    color: "gray"
  }
};

// 유형 3: BoxInBox (topItems 또는 bottomItems가 있으면 자동으로 BoxInBox형)
const boxInBoxData = {
  title: "타이틀",
  topItems: [
    { title: "메인텍스트", tooltip: "소득 유형", content: "급여소득자", tooltipPlacement: "top-center" },
    { title: "메인텍스트", tooltip: "카드사", content: "신한카드", tooltipPlacement: "top-center" },
  ],
  bottomItems: [
    { title: "메인텍스트", tooltip: "회사 주소", content: "서울특별시 중구 을지로 100", tooltipPlacement: "top-center" },
    { title: "메인텍스트", tooltip: "회사 연락처", content: "02-542-1987", tooltipPlacement: "top-center" },
  ],
  cardStyle: {
    variant: "outline"
  },
  innerCardStyle: {
    variant: "solid",
    color: "gray"
  }
};

// 유형 5: 복합 유형 (Composite Type)
// 모든 속성을 가진 복합 데이터
// 우선순위: expandedItems > topItems/bottomItems > cardStyle > items
const compositeData = {
  // Basic 속성
  title: "복합 데이터 리스트",
  items: [
    { title: "기본 항목 1", content: "일반 데이터" },
    { title: "기본 항목 2", content: "일반 데이터 2" }
  ],
  
  // Expandable 속성 (최우선 - 이게 있으면 Expandable로 동작)
  expandedItems: [
    { title: "확장 항목 1", content: "확장된 데이터" },
    { title: "확장 항목 2", content: "확장된 데이터 2" }
  ],
  expandLabel: "자세히 보기",
  collapseLabel: "접기",
  
  // BoxInBox 속성 (expandedItems가 없다면 이것이 우선)
  topItems: [
    { title: "상단 그룹", content: "Top 데이터" }
  ],
  bottomItems: [
    { title: "하단 그룹", content: "Bottom 데이터" }
  ],
  
  // Box 속성 (위 두 개가 없다면 이것이 우선)
  cardStyle: {
    variant: "solid",
    color: "gray"
  },
  innerCardStyle: {
    variant: "solid",
    color: "gray"
  }
};

// ⚠️ 실제 동작:
// compositeData는 expandedItems가 있으므로 "Expandable" 타입으로 동작
// topItems, bottomItems, cardStyle은 무시됨 (우선순위 규칙)
</script>

<style lang="scss" scoped>
@use "@assets/styles/module/_data-list" as *; // 모듈 영역 스타일

// 커스텀 슬롯 스타일
/*
.demo-subtitle {
  font-size: 14px;
  font-weight: 600;
  color: #666;
  margin: 12px 0 8px 0;
}

.custom-header {
  background: #f0f8ff;
  padding: 12px;
  border-radius: 4px;
  margin-bottom: 8px;
  
  h3 {
    margin: 0;
    font-size: 16px;
    color: #007aff;
  }
  
  button {
    margin-top: 8px;
    padding: 4px 12px;
    background: #007aff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    
    &:hover {
      background: #0056b3;
    }
  }
}

.custom-badge {
  background: #e3f2fd;
  padding: 8px 12px;
  border-radius: 4px;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
}

.custom-divider {
  text-align: center;
  padding: 8px;
  color: #999;
  font-size: 12px;
}

.custom-notice {
  background: #fff3cd;
  padding: 8px 12px;
  border-radius: 4px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #856404;
}

.text-muted {
  color: #6c757d;
  font-size: 12px;
  display: block;
  margin-top: 4px;
}

.custom-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  padding: 8px 0;
  border-bottom: 2px solid #e0e0e0;
  margin-bottom: 12px;
}

.badge-new {
  display: inline-block;
  background: #ff4444;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: bold;
  margin-right: 8px;
}

.custom-footer {
  padding: 8px 0;
  border-top: 1px solid #e0e0e0;
  margin-top: 12px;
  text-align: right;
  
  small {
    color: #999;
    font-size: 12px;
  }
}*/
</style>
