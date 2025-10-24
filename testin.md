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
</route>

<template>
  <div class="demo-title">유형 : [module] Scdatalist</div>
  <section class="section">
    <ScDataList
      :items="scBasicItems"
      title="Data List Title"
      variant="basic"
    >
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
  <div class="demo-title">유형 : [module] Expandable datalist</div>
  <ExpandableDataList
    :items="expandableItems"
    class="mt-3"
  />
  <div class="demo-title">유형 1 : DataList 기본 (ScDataList 사용)</div>
  <!-- S : 유형 1 : DataList 기본 -->
  <ScDataList
    :items="basicItemsForSc"
    variant="basic"
    align="spaceBetween"
  />
  <!-- E : 유형 1 : DataList 기본 -->

  <div class="demo-title">유형 2 : DataList - box (ScDataList 사용)</div>
  <!-- S : 유형 2 : DataList - box -->
  <ScDataList
    :items="grayItemsForSc"
    variant="box"
    cardVariant="solid"
    cardColor="gray"
    align="spaceBetween"
  />
  <!-- E : 유형 2 : DataList - box -->

  <div class="demo-title">유형 3 : DataList - box in box (ScDataList 사용)</div>
  <!-- S : 유형 3 : DataList - box in box -->
  <ScDataList
    :items="[]"
    title="타이틀"
    variant="boxInBox"
    cardVariant="outline"
    innerCardVariant="solid"
    innerCardColor="gray"
    :topGroupItems="boxInBoxTopItemsForSc"
    :bottomGroupItems="boxInBoxBottomItemsForSc"
    align="spaceBetween"
  >
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
</template>

<script setup>
import {
  BoxButton,
  BoxButtonGroup,
  Divider
} from "@shc-nss/ui/solid";
import ExpandableDataList from "../../_module/ExpandableDataList.vue";
import ScDataList from "../../_module/ScDataList.vue";

// 기본형 리스트 아이템들 (기존 방식)
const basicItems = [
  { tooltip: "설명 툴팁", content: "김신한" },
  { tooltip: "영문 표기", content: "KIM SHIN HAN" },
  { tooltip: "외국인 번호", content: "901231-1******", small: "(외국인 번호)" },
  { tooltip: "주소", content: "03468 서울특별시 중구 을지 1길 10, 101동 101호(을지로 1가)" },
  { tooltip: "연락처", content: "010-1234-5678" },
];

// 기본형 리스트 아이템들 (ScDataList 형식)
const basicItemsForSc = [
  { title: "메인텍스트", tooltip: "설명 툴팁", content: "김신한" },
  { title: "메인텍스트", tooltip: "영문 표기", content: "KIM SHIN HAN" },
  { title: "메인텍스트", tooltip: "외국인 번호", content: "901231-1******", small: "(외국인 번호)" },
  { title: "메인텍스트", tooltip: "주소", content: "03468 서울특별시 중구 을지 1길 10, 101동 101호(을지로 1가)" },
  { title: "메인텍스트", tooltip: "연락처", content: "010-1234-5678" },
];
// 기본형 sc data list items
const scBasicItems = [
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
];
// 확장형 data-list
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

// Gray 카드(박스) 내부 리스트 (기존 방식)
const grayItems = [
  { tooltip: "국가", content: "[KR] KOREA, REPUBLIC OF" },
  { tooltip: "거주여부", content: "거주" },
  { tooltip: "성별", content: "남자" },
];

// Gray 카드(박스) 내부 리스트 (ScDataList 형식)
const grayItemsForSc = [
  { title: "메인텍스트", tooltip: "국가", content: "[KR] KOREA, REPUBLIC OF", tooltipPlacement: "top-center" },
  { title: "메인텍스트", tooltip: "거주여부", content: "거주", tooltipPlacement: "top-center" },
  { title: "메인텍스트", tooltip: "성별", content: "남자", tooltipPlacement: "top-center" },
];

// 혼합형 카드 상단(Gray) 리스트 (기존 방식)
const boxInBoxTopItems = [
  { tooltip: "소득 유형", content: "급여소득자" },
  { tooltip: "카드사", content: "신한카드" },
];

// 혼합형 카드 상단(Gray) 리스트 (ScDataList 형식)
const boxInBoxTopItemsForSc = [
  { title: "메인텍스트", tooltip: "소득 유형", content: "급여소득자", tooltipPlacement: "top-center" },
  { title: "메인텍스트", tooltip: "카드사", content: "신한카드", tooltipPlacement: "top-center" },
];

// 혼합형 카드 하단(기본형) 리스트 (기존 방식)
const boxInBoxBottomItems = [
  { tooltip: "회사 주소", content: "서울특별시 중구 을지로 100" },
  { tooltip: "회사 연락처", content: "02-542-1987" },
];

// 혼합형 카드 하단(기본형) 리스트 (ScDataList 형식)
const boxInBoxBottomItemsForSc = [
  { title: "메인텍스트", tooltip: "회사 주소", content: "서울특별시 중구 을지로 100", tooltipPlacement: "top-center" },
  { title: "메인텍스트", tooltip: "회사 연락처", content: "02-542-1987", tooltipPlacement: "top-center" },
];
</script>

<style lang="scss" scoped>
@use "@assets/styles/module/_data-list" as *; // 모듈 영역 스타일
</style>
