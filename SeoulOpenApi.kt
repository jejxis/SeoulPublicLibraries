package com.jejxis.seoulpubliclibraries

import com.jejxis.seoulpubliclibraries.data.Library
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

class SeoulOpenApi {
    companion object{
        val DOMAIN = "http://openapi.seoul.go.kr:8088/"
        val API_KEY = "KEYKEYKEYTKEYTEKYEKYEKHEKHEKHE"//데이터 광장에서 받은 
    }
}
interface SeoulOpenService{//레트로핏에서 사용
    @GET("/{api_key}/json/SeoulPublicLibraryInfo/1/200")//호출할 주소 지정..레트로핏에서 사용시 이 주소와 DOMAIN 조합해서 사용할 것임.
    fun getLibrary(@Path("api_key")key: String): Call<Library>//파라미터 key가 {api_key}자리에 들어감.
}
