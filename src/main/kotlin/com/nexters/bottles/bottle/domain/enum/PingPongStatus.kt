package com.nexters.bottles.bottle.domain.enum

enum class PingPongStatus {
    NONE,    // 아직 결정하지 않은 상황
    ACTIVE,  // 보틀 핑퐁이 진행 중인 상황
    STOPPED, // 보틀을 한쪽이 거절한 상황
    MATCHED, // 매칭이 된 상황
}
