package com.example.chalpu.home.domain;

import java.util.Arrays;
import java.util.Optional;

public enum PhotoTip {
    ANGLE_45("1", "45도 각도의 마법", "대부분의 음식은 45도 각도에서 촬영하면 입체감이 살아나요. 너무 위에서도, 너무 옆에서도 말고 적당한 각도가 포인트!"),
    NATURAL_LIGHT("2", "자연광 활용", "자연광을 이용하면 음식이 더 맛있어 보여요. 그림자가 너무 강하면 얇은 흰 천으로 부드럽게 만들어보세요."),
    SIMPLE_BACKGROUND("3", "심플한 배경", "음식이 돋보이게 하려면 배경을 단순하게! 너무 많은 소품은 오히려 방해가 될 수 있어요."),
    COLOR_POINT("4", "컬러 포인트", "접시나 냅킨 등 소품으로 색상 포인트를 주면 사진이 더 생동감 있어 보여요."),
    TOP_SHOT("5", "수직샷의 활용", "피자, 샐러드처럼 평평한 음식은 위에서 수직으로 찍으면 구성이 잘 드러나요."),
    PROPER_DISTANCE("6", "적당한 거리 유지", "너무 가까이서 찍으면 왜곡될 수 있으니, 적당한 거리를 유지하세요."),
    FOCUS("7", "포커스와 아웃포커스", "주인공 음식에 초점을 맞추고, 배경은 흐리게 처리하면 집중도가 높아져요."),
    WARM_TONE("8", "따뜻한 색감", "음식 사진은 따뜻한 색감이 식욕을 자극합니다. 화이트밸런스를 조정해보세요."),
    ACTION("9", "움직임 포착", "소스를 붓거나, 김이 나는 순간 등 움직임을 담으면 사진이 더 생동감 있어요."),
    NATURAL("10", "자연스러운 연출", "너무 인위적인 세팅보다는 자연스럽게 흐트러진 모습이 더 맛있어 보일 수 있어요.");

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