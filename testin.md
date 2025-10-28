<route lang="yaml">
meta:
  id: data-list
  title: DataList
  menu: Module > DataList Module
  layout: EmptyLayout
  category: Module
  publish: 김대민
  publishVersion: 0.8
  navbar: false
  etc: (공통팀)이강
</route>

<template>
  <div class="demo-title">유형 0 : Basic DataList (기본형 - 배열)</div>
  <section class="section">
    <ScDataList
      :items="scBasicItems"
      :listTitle="listTitle"
    >
      <!-- 커스텀 슬롯 예시: 특정 아이템만 커스터마이징 -->
      <template #item-4-content="{ item }">
        <div class="data-list__btn-wrap">
          <BoxButton
            color="tertiary"
            text="메모 입력하기"
          />
        </div>
      </template>
    </ScDataList>
  </section>

  <Divider
    variant="basic"
    color="tertiary"
    class="my-6"
  />

  <div class="demo-title">유형 1 : Expandable DataList (확장형)</div>
  <section class="section">
    <ScDataList
      :items="expandableItems"
      expandLabel="더보기"
      collapseLabel="접기"
    />
  </section>

  <Divider
    variant="basic"
    color="tertiary"
    class="my-6"
  />

  <div class="demo-title">유형 2 : Box DataList (Gray 박스)</div>
  <section class="section">
    <ScDataList :items="grayItems" />
  </section>

  <Divider
    variant="basic"
    color="tertiary"
    class="my-6"
  />

  <div class="demo-title">유형 3 : BoxInBox DataList (박스 안에 박스)</div>
  <section class="section">
    <ScDataList :items="boxInBoxItems">
      <!-- Actions 슬롯 활용 -->
      <template #actions>
        <BoxButtonGroup variant="100">
          <BoxButton
            color="secondary"
            size="medium"
            text="확인"
          />
        </BoxButtonGroup>
      </template>
    </ScDataList>
  </section>

  <Divider
    variant="basic"
    color="tertiary"
    class="my-6"
  />

  <div class="demo-title">고급 : 커스텀 슬롯 활용 예시</div>
  <section class="section">
    <ScDataList :items="customItems">
      <!-- 첫 번째 아이템 title 커스터마이징 -->
      <template #item-0-title="{ item }">
        <div style="display: flex; align-items: center; gap: 8px;">
          <span style="background: #1e88e5; color: white; padding: 2px 8px; border-radius: 4px; font-size: 12px;">
            VIP
          </span>
          <span class="data-list__text">{{ item.title }}</span>
        </div>
      </template>

      <!-- 두 번째 아이템 content 커스터마이징 -->
      <template #item-1-content="{ item }">
        <div style="display: flex; align-items: center; gap: 8px;">
          <span style="color: #f44336; font-weight: bold;">{{ item.content }}</span>
          <small style="color: #999;">(할인 적용)</small>
        </div>
      </template>
    </ScDataList>
  </section>
</template>

<script setup>
import {
  BoxButton,
  BoxButtonGroup,
  Divider,
} from "@shc-nss/ui/solid";
import { ref } from "vue";
import ScDataList from "../../_module/ScDataList.vue";

// ============================================================================
// 데이터 구조 예시
// ============================================================================

const listTitle = ref("Data List Title");

// 유형 0: Basic (기본 배열)
const scBasicItems = [
  { title: "출금가능금액", content: "50,000원", tooltip: "tooltip content" },
  { title: "금리", content: "연 1.00%" },
  { title: "계좌개설일", content: "2020.04.04" },
  {
    title: "자동 이체 현황",
    contentBtnText: "3건",
  },
  { 
    title: "메모", 
    content: "" 
    // content는 슬롯으로 대체됨
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
];

// 유형 1: Expandable (확장형)
// ✅ defaultItems: 기본 표시 아이템
// ✅ expandedItems: 확장 시 표시 아이템
const expandableItems = {
  defaultItems: [
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
};

// 유형 2: Box (Gray 단일 박스)
// ✅ boxItems: Gray 박스 내부 아이템
const grayItems = {
  boxItems: [
    { title: "국가", tooltip: "국가", content: "[KR] KOREA, REPUBLIC OF" },
    { title: "거주여부", tooltip: "거주여부", content: "거주" },
    { title: "성별", tooltip: "성별", content: "남자" },
  ],
};

// 유형 3: BoxInBox (박스 안에 박스)
// ✅ title: 카드 상단 타이틀 (선택)
// ✅ topItems: 상단 Gray 박스 아이템
// ✅ mainItems: 중간 일반 영역 아이템 (선택)
// ✅ bottomItems: 하단 일반 영역 아이템 (선택)
const boxInBoxItems = {
  title: "직원 정보",
  topItems: [
    { title: "소득 유형", tooltip: "소득 유형", content: "급여소득자" },
    { title: "카드사", tooltip: "카드사", content: "신한카드" },
  ],
  mainItems: [
    { title: "성명", content: "김신한" },
    { title: "직급", content: "대리" },
  ],
  bottomItems: [
    { title: "회사 주소", tooltip: "회사 주소", content: "서울특별시 중구 을지로 100" },
    { title: "회사 연락처", tooltip: "회사 연락처", content: "02-542-1987" },
  ],
};

// 커스텀 슬롯 예시용
const customItems = [
  { title: "고객명", content: "김신한" },
  { title: "등급", content: "VVIP" },
  { title: "포인트", content: "10,000P" },
];
</script>

<style lang="scss" scoped>
@use "@assets/styles/module/_data-list" as *;
</style>
