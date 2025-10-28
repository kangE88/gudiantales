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
  <div class="demo-title">유형 : [module] Scdatalist</div>
  <section class="section">
    <!-- <DataList
      v-for="(item, itemIndex) in basicItems"
      align="spaceBetween"
    >
      <template #title>
        <span class="data-list__text">{{ item.title }}</span>
        <Tooltip
          v-if="item.tooltip"
          placement="top-left"
          :showClose="true"
          :content="item.tooltip"
        />
      </template>
      <template #content>
        <ToggleSwitch v-if="item.showSwitch" />
        <span
          v-if="item.content"
          class="data-list__text"
          v-html="item.content"
        />
        <TextButton
          v-if="item.contentBtnText"
          color="secondary"
          size="small"
          :text="item.contentBtnText"
          :rightIcon="{ iconName: 'Chevron_right' }"
          class="font-weight-300 spacing-none"
        />
        <div class="data-list__btn-wrap">
          <BoxButton
            v-if="item.memo"
            color="tertiary"
            text="메모 입력하기"
          />
        </div>
      </template>
    </DataList> -->

    <!-- 유형 1 == :items="scBasicItems" -->
    <ScDataList
      :items="scBasicItems"
      :listTitle="listTitle"
    >
    </ScDataList>
    <Divider
      variant="basic"
      color="tertiary"
      class="my-6"
    />
  </section>
  <div class="demo-title">유형 : [module] Expandable datalist</div>
  <!-- <ExpandableDataList
    :items="expandableItems"
    class="mt-3"
  /> -->
  <!-- 펼쳐짐 :defaultExpanded="true" -->
  <ScDataList
    :items="expandableItems"
    :listTitle="listTitle"
  >
  </ScDataList>
  <div class="demo-title">유형 1 : DataList 기본</div>
  <!-- S : 유형 1 : DataList 기본 -->
  <div class="sc-data__list">
    <div class="data-list__group">
      <DataList
        v-for="(item, i) in basicItems"
        :key="`basic-${i}`"
        align="spaceBetween"
      >
        <template #title>
          <span class="data-list__text">메인텍스트</span>
          <Tooltip
            placement="top-center"
            :content="item.tooltip"
          />
          <small v-if="item.small">{{ item.small }}</small>
        </template>
        <template #content>
          <span class="data-list__text">{{ item.content }}</span>
        </template>
      </DataList>
    </div>
  </div>
  <!-- E : 유형 1 : DataList 기본 -->

  <div class="demo-title">유형 2 : DataList - box</div>
  <ScDataList
    :items="grayItems"
    :listTitle="listTitle"
  >
  </ScDataList>
  <!-- S : 유형 2 : DataList - box -->
  <!-- <div class="sc-data__list">
    <BasicCard
      variant="solid"
      color="gray"
    >
      <div class="data-list__group">
        <DataList
          v-for="(item, i) in grayItems"
          :key="`gray-${i}`"
          align="spaceBetween"
        >
          <template #title>
            <span class="data-list__text">메인텍스트</span>
            <Tooltip
              placement="top-center"
              :content="item.tooltip"
            />
          </template>
          <template #content>
            <span class="data-list__text">{{ item.content }}</span>
          </template>
        </DataList>
      </div>
    </BasicCard>
  </div> -->
  <!-- E : 유형 2 : DataList - box -->

  <div class="demo-title">유형 3 : DataList - box in box</div>
  <ScDataList
    :items="boxInBoxItems"
    :listTitle="listTitle"
  >
  </ScDataList>
  <!-- S : 유형 3 : DataList - box in box -->
  <!-- <div class="sc-data__list">
    <BasicCard variant="outline">
      <!-- 카드 상단 타이틀 
  <h3 class="data-list__title">타이틀</h3>

  <!-- gray 카드(Box) 활용 
      <BasicCard
        variant="solid"
        color="gray"
      >
        <div class="data-list__group">
          <DataList
            v-for="(item, i) in boxInBoxTopItems"
            :key="`mix-top-${i}`"
            align="spaceBetween"
          >
            <template #title>
              <span class="data-list__text">메인텍스트</span>
              <Tooltip
                placement="top-center"
                :content="item.tooltip"
              />
            </template>
            <template #content>
              <span class="data-list__text">{{ item.content }}</span>
            </template>
          </DataList>
        </div>
      </BasicCard>

      <!-- 하단 일반 그룹 (기본형) 
      <div class="data-list__group">
        <DataList
          v-for="(item, i) in boxInBoxBottomItems"
          :key="`mix-bottom-${i}`"
          align="spaceBetween"
        >
          <template #title>
            <span class="data-list__text">메인텍스트</span>
            <Tooltip
              placement="top-center"
              :content="item.tooltip"
            />
          </template>
          <template #content>
            <span class="data-list__text">{{ item.content }}</span>
          </template>
        </DataList>
      </div>

      <template #actions>
        <BoxButtonGroup variant="100">
          <BoxButton
            color="secondary"
            size="medium"
            text="텍스트"
          />
        </BoxButtonGroup>
      </template>
    </BasicCard>
  </div> -->
  <!-- E : 유형 3 : DataList - box in box -->
</template>

<script setup>
import { DataList, Divider, Tooltip } from "@shc-nss/ui/solid";
import ScDataList from "../../_module/ScDataList.vue";

import { ref } from "vue";

// 기본형 리스트 아이템들
const basicItems = [
  { title: "성명", tooltip: "설명 툴팁", content: "김신한" },
  { title: "영문성명", tooltip: "영문 표기", content: "KIM SHIN HAN" },
  {
    title: "주민등록번호",
    tooltip: "외국인 번호",
    content: "901231-1******",
    small: "(외국인 번호)",
  },
  {
    title: "주소",
    tooltip: "주소",
    content: "03468 서울특별시 중구 을지 1길 10, 101동 101호(을지로 1가)",
  },
  { title: "연락처", tooltip: "연락처", content: "010-1234-5678" },
];
// 기본형 sc data list items
const listTitle = ref("Data List Title");
const scBasicItems = [
  { title: "출금가능금액", content: "50,000원", tooltip: "tooltip content" },
  { title: "금리", content: "연 1.00%" },
  { title: "계좌개설일", content: "2020.04.04" },
  {
    title: "자동 이체 현황",
    contentBtnText: "3건",
  },
  { title: "", content: "", boxButtonText: "메모 입력하기" },
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

// Gray 카드(박스) 내부 리스트
const grayItems = {
  boxItems: [
    { title: "grayItem1", tooltip: "국가", content: "[KR] KOREA, REPUBLIC OF" },
    { title: "grayItem2", tooltip: "거주여부", content: "거주" },
    { title: "grayItem3", tooltip: "성별", content: "남자" },
  ],
};

// 혼합형 카드 상단(Gray) 리스트
const boxInBoxItems = {
  boxItem1: [
    { title: "grayItem1", tooltip: "국가", content: "[KR] KOREA, REPUBLIC OF" },
    { title: "grayItem2", tooltip: "거주여부", content: "거주" },
    { title: "grayItem3", tooltip: "성별", content: "남자" },
  ],
  boxItem2: [
    { tooltip: "소득 유형", content: "급여소득자" },
    { tooltip: "카드사", content: "신한카드" },
  ],
};

// 혼합형 카드 하단(기본형) 리스트
// const boxInBoxBottomItems = [
//   { tooltip: "회사 주소", content: "서울특별시 중구 을지로 100" },
//   { tooltip: "회사 연락처", content: "02-542-1987" },
// ];
</script>

<style lang="scss" scoped>
@use "@assets/styles/module/_data-list" as *; // 모듈 영역 스타일
</style>
