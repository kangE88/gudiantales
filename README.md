<route lang="yaml">
meta:
  id: useSortableList
  title: useSortableList 테스트
  menu: useSortableList
  layout: SubLayout
  category: uiUtils
  publish: 이강
  publishVersion: 0.8
  # 네비게이션 상세 옵션
  header:
    variant: sub
    fixed: true
    showBack: true
    close: true
</route>
<template>
  <div class="sc-list sc-select__list">
    <div class="select-list__group select-list__image">
      <SelectBoxGroup
        v-model="pickIndex"
        orientation="vertical"
        variant="solid"
        as="div"
        :items="basicListItems"
        ref="el"
      >
        <!-- v-model="imgValue" -->
        <template #contents="{ item }">
          <ListItem :left="{ mainText: item.main, subText: item.sub }">
            <template #leftIcon>
              <img
                v-if="item.image"
                :src="item.image"
                alt=""
                class="thumb"
                @click="onClickItem(item)"
              />
            </template>
            <template #rightIcon>
              <IconButton
                iconName="Menu"
                size="medium"
                aria-label="드래그하여 순서 변경"
                @click.stop
              />
            </template>
          </ListItem>
        </template>
      </SelectBoxGroup>
    </div>
  </div>
  <section
    class="section"
    ref="contentRef2"
  ></section>
  <BottomActionContainer :scrollDim="true">
    <BoxButtonGroup size="xlarge">
      <BoxButton
        text="완료"
        :disabled="pickIndex !== undefined ? false : true"
      />
    </BoxButtonGroup>
  </BottomActionContainer>
</template>

<script setup>
import { useSortableCustom, useSortableList } from "@shc-nss/shared/utils";
import {
  BottomActionContainer,
  BoxButton,
  BoxButtonGroup,
  IconButton,
  ListItem,
  SelectBoxGroup,
} from "@shc-nss/ui/solid";
import { ref, watch } from "vue";
const el = ref();
const pickIndex = ref();
const onClickItem = (item) => {
  pickIndex.value = item.value;
};

// 데모용 이미지 (images 안의 샘플)
import imgSample1 from "@assets/images/pages/demo/img-sample1.png";
import imgSample2 from "@assets/images/pages/demo/img-sample2.png";

const basicListItems = ref([
  {
    label: "신한 Deep Dream",
    value: "i1", //셀렉티드 값
    main: "신한 Deep Dream",
    sub: "서브텍스트",
    image: imgSample1,
  },
  {
    label: "신한 Deep Dream",
    value: "i2", //셀렉티드 값
    main: "신한 Deep Dream",
    sub: "서브텍스트",
    image: imgSample2,
  },
  {
    label: "신한 Deep Dream",
    value: "i3", //셀렉티드 값
    main: "신한 Deep Dream",
    sub: "서브텍스트",
    image: imgSample1,
  },
  {
    label: "신한 Deep Dream",
    value: "i4", //셀렉티드 값
    main: "신한 Deep Dream",
    sub: "서브텍스트",
    image: imgSample2,
  },
  {
    label: "신한 Deep Dream",
    value: "i5", //셀렉티드 값
    main: "신한 Deep Dream",
    sub: "서브텍스트",
    image: imgSample1,
  },
  {
    label: "신한 Deep Dream",
    value: "i6", //셀렉티드 값
    main: "신한 Deep Dream",
    sub: "서브텍스트",
    image: imgSample2,
  },
  {
    label: "신한 Deep Dream",
    value: "i7", //셀렉티드 값
    main: "신한 Deep Dream",
    sub: "서브텍스트",
    image: imgSample1,
  },
  {
    label: "신한 Deep Dream",
    value: "i8", //셀렉티드 값
    main: "신한 Deep Dream",
    sub: "서브텍스트",
    image: imgSample2,
  },
  {
    label: "신한 Deep Dream",
    value: "i9", //셀렉티드 값
    main: "신한 Deep Dream",
    sub: "서브텍스트",
    image: imgSample1,
  },
  {
    label: "신한 Deep Dream",
    value: "i10", //셀렉티드 값
    main: "신한 Deep Dream",
    sub: "서브텍스트",
    image: imgSample2,
  },
  {
    label: "신한 Deep Dream",
    value: "i11", //셀렉티드 값
    main: "신한 Deep Dream",
    sub: "서브텍스트",
    image: imgSample1,
  },
  {
    label: "신한 Deep Dream",
    value: "i12", //셀렉티드 값
    main: "신한 Deep Dream",
    sub: "서브텍스트",
    image: imgSample2,
  },
  {
    label: "신한 Deep Dream",
    value: "i13", //셀렉티드 값
    main: "신한 Deep Dream",
    sub: "서브텍스트",
    image: imgSample1,
  },
  {
    label: "신한 Deep Dream",
    value: "i14", //셀렉티드 값
    main: "신한 Deep Dream",
    sub: "서브텍스트",
    image: imgSample2,
  },
  {
    label: "신한 Deep Dream",
    value: "i15", //셀렉티드 값
    main: "신한 Deep Dream",
    sub: "서브텍스트",
    image: imgSample1,
  },
  {
    label: "신한 Deep Dream",
    value: "i16", //셀렉티드 값
    main: "신한 Deep Dream",
    sub: "서브텍스트",
    image: imgSample2,
  },
  {
    label: "신한 Deep Dream",
    value: "i17", //셀렉티드 값
    main: "신한 Deep Dream",
    sub: "서브텍스트",
    image: imgSample1,
  },
  {
    label: "신한 Deep Dream",
    value: "i18", //셀렉티드 값
    main: "신한 Deep Dream",
    sub: "서브텍스트",
    image: imgSample2,
  },
  {
    label: "신한 Deep Dream",
    value: "i19", //셀렉티드 값
    main: "신한 Deep Dream",
    sub: "서브텍스트",
    image: imgSample1,
  },
  {
    label: "신한 Deep Dream",
    value: "i20", //셀렉티드 값
    main: "신한 Deep Dream",
    sub: "서브텍스트",
    image: imgSample2,
  },
]);
useSortableList(el, basicListItems);

useSortableCustom(el, basicListItems);

watch(basicListItems, (newValue) => {
  console.log("basicListItems::", basicListItems.value);
});
watch(pickIndex, (newValue) => {
  console.log("pickIndex::", pickIndex.value);
});

// useSortable(el, basicListItems, {
//   animation: 150, //드래그 애니메이션 속도
//   scroll: true, //자동스크롤 활성화
//   scrollSensitivity: 30, //가장자리에서 30px 이내로 가면 스크롤 시작
//   scrollSpeed: 20, // 스크롤 속도
//   //bubbleScroll: true, //부모요소도 스크롤 가능
//   forceAutoScrollFallback: true, // kick
// });
</script>
