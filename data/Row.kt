package com.jejxis.seoulpubliclibraries.data

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class Row(
    val ADRES: String,
    val CODE_VALUE: String,
    val FDRM_CLOSE_DATE: String,
    val GU_CODE: String,
    val HMPG_URL: String,
    val LBRRY_NAME: String,
    val LBRRY_SEQ_NO: String,
    val LBRRY_SE_NAME: String,
    val OP_TIME: String,
    val TEL_NO: String,
    val XCNTS: String,
    val YDNTS: String
): ClusterItem {//맵 클러스터링
    //마커에 해당하는 클래스가 Row...
    override fun getPosition(): LatLng {
        return LatLng(XCNTS.toDouble(), YDNTS.toDouble())//개별 마커가 표시될 좌표
    }

    override fun getTitle(): String? {
        return LBRRY_NAME//마커 클릭시 나타나는 타이틀
    }

    override fun getSnippet(): String? {
        return ADRES//마커 클릭시 나타나는 서브 파이틀
    }

    override fun hashCode(): Int {//값 중 null이 있으면 hashCode 생성시 오류 발생
        return LBRRY_SEQ_NO.toInt()
    }
}