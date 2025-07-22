package com.example.chalpu.home.domain;

import java.util.Arrays;
import java.util.Optional;

public enum PhotoTip {
    ANGLE_45("1", "45도 각도의 마법", "대부분의 음식은 45도 각도에서 촬영하면 입체감이 살아나요. 너무 위에서도, 너무 옆에서도 말고 적당한 각도가 포인트!"),
    BACKGROUND("2", "배경 정리하기", "음식 뒤에 보이는 배경을 깔끔하게 정리하세요. 단순한 배경일수록 음식이 더 돋보여요. 흰색 접시나 나무 테이블이 좋아요!"),
    COLOR_CONTRAST("3", "색깔 대비 활용하기", "빨간 음식은 흰색이나 검은색 배경에, 하얀 음식은 어두운 배경에 놓으면 더 선명하게 보여요. 색깔 대비를 활용해보세요!"),
    PROPS("4", "소품으로 분위기 연출", "젓가락, 냅킨, 작은 반찬 등을 자연스럽게 배치하면 더 풍성해 보여요. 하지만 너무 많이 놓으면 복잡해 보이니 주의하세요!"),
    STEAM("5", "스팀과 김 활용하기", "뜨거운 음식에서 올라오는 김을 촬영하면 따뜻함이 전달돼요. 국물 요리나 찜 요리를 찍을 때 김이 보이도록 빠르게 촬영하세요!"),
    SECTION("6", "음식의 단면 보여주기", "햄버거, 샌드위치, 케이크 등은 반으로 잘라서 속재료를 보여주면 더 맛있어 보여요. 단면이 깔끔하게 보이도록 날카로운 칼을 사용하세요!"),
    AMOUNT("7", "양 조절하기", "접시에 음식을 너무 가득 담지 마세요. 접시의 80% 정도만 채우면 더 고급스러워 보여요. 여백의 미를 활용해보세요!"),
    TIME("8", "시간대별 촬영 팁", "오전 10시~오후 2시 사이가 자연광이 가장 좋아요. 흐린 날에도 창가 근처에서 촬영하면 부드러운 빛을 얻을 수 있어요!");

    private final String id;
    private final String title;
    private final String text;

    PhotoTip(String id, String title, String text) {
        this.id = id;
        this.title = title;
        this.text = text;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getText() { return text; }

    public static Optional<PhotoTip> findById(String id) {
        return Arrays.stream(values())
                .filter(tip -> tip.id.equals(id))
                .findFirst();
    }
} 