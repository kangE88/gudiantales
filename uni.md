<route lang="yaml">
meta:
  id: SAT002A01
  title: 금융캘린더
  menu: 자산 > 지출관리 > 금융캘린더 > 메인_달력형
  layout: SubLayout
  category: 자산
  publish: 남아랑
  publishVersion: 0.8
  status: 작업완료
  etc: 251105(DatePicker 알림닷 추가, 연월표기 수정)
  header:
    fixed: true
    back: true
</route>

<template>
  <div class="sc-contents__body">
    <section class="section calendar-header-wrap">
      <div class="calendar-header">
        <TextDropdown
          value="2025.11"
          size="xlarge"
        />
        <SegmentSwitch
          :items="iconItems"
          size="small"
          :modelValue="false"
        />
      </div>
      <div class="calendar-header__tool-group">
        <div class="calendar-header__total-container">
          <span class="calendar-header__total-title">지출</span>
          <span class="calendar-header__total-result">10,456,000원</span>
        </div>
        <IconButton
          :color="false"
          :disabled="false"
          iconName="Circle_info"
          size="small"
        />
      </div>
    </section>
    <div class="calendar-datepicker-wrap">
      <!-- 알람닷 : button.sv-datepicker__day-btn에 .sv-datepicker__day-btn--alarm 적용 -->
      <ScheduleDatePicker
        :data="data"
        defaultView="monthly"
      >
        <template #day-content="{ data }">
          {{ data }}
          <!-- <div
            v-for="d in data"
            :key="data.date"
          >
            {{ d.date }}
          </div> -->
        </template>
      </ScheduleDatePicker>
    </div>
  </div>

  <BottomActionContainer
    class="calendar-bottom-container"
    :scrollDim="true"
  >
    <CapsuleButton
      :leftIcon="{ iconName: 'edit' }"
      text="작성"
      variant="tonal"
      size="large"
    />
  </BottomActionContainer>
</template>
<script setup>
import {
  BottomActionContainer,
  CapsuleButton,
  IconButton,
  ScheduleDatePicker,
  SegmentSwitch,
  TextDropdown,
} from "@shc-nss/ui/solid";
import { isSameDay } from "date-fns";
const iconItems = [
  {
    iconName: "calender",
  },
  {
    iconName: "Menu",
  },
];
const data = [
  {
    id: 1,
    type: "label",
    labelColor: "blue",
    date: "2025-11-10T10:00:14.291Z",
    title: "테스트테스트",
  },
  {
    id: 2,
    type: "income",
    date: "2025-11-11T10:00:14.291Z",
    title: "+1111111",
    class: "sv-datepicker__day-btn--alarm",
  },
  {
    id: 3,
    type: "expense",
    date: "2025-11-12T10:00:14.291Z",
    title: "-1234234",
  },
  {
    id: 4,
    type: "label",
    labelColor: "green",
    date: "2025-11-13T10:00:14.291Z",
    title: "테스트테스트",
  },
  {
    id: 5,
    type: "label",
    labelColor: "blue",
    date: "2025-11-14T10:00:14.291Z",
    title: "테스트테스트",
  },
];

function getIcon(targetDate) {
  return data.find(({ date }) => isSameDay(date, targetDate))?.icon;
}
</script>
