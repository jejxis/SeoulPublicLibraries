package com.jejxis.seoulpubliclibraries

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.jejxis.seoulpubliclibraries.data.Library
import com.jejxis.seoulpubliclibraries.data.Row
import com.jejxis.seoulpubliclibraries.databinding.ActivityMapsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var clusterManager: ClusterManager<Row>
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //클러스터 매니저 세팅
        clusterManager = ClusterManager(this, mMap)
        mMap.setOnCameraIdleListener(clusterManager)//화면 이동 후 멈췄을 때 설정
        mMap.setOnMarkerClickListener(clusterManager)//마커 클릭 설정
        loadLibraries()
//        mMap.setOnMarkerClickListener {
//            if(it.tag != null){//tag가 null이 아니면
//                var url = it.tag as String//String으로 형변환해서
//                if(!url.startsWith("http")){//http로 시작하지 않으면
//                    url = "http://${url}"//앞에 붙여준다.
//                }
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))//url을 intent로 생성
//                startActivity(intent)//액티비티 생성
//            }
//            true
//        }
    }
    fun loadLibraries(){
        val retrofit = Retrofit.Builder()//레트로핏 설정
            .baseUrl(SeoulOpenApi.DOMAIN)//도메인 주소
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val seoulOpenService = retrofit.create(SeoulOpenService::class.java)//인터페이스를 서비스 객체로 반환

        seoulOpenService
            .getLibrary(SeoulOpenApi.API_KEY)
            .enqueue(object : Callback<Library> {
                override fun onResponse(call: Call<Library>, response: Response<Library>) {
                    showLibraries(response.body() as Library)//지도에 마커 표시
                }

                override fun onFailure(call: Call<Library>, t: Throwable) {//서버 요청 실패했을 때
                    Toast.makeText(baseContext, "서버에서 데이터를 가져올 수 없습니다.", Toast.LENGTH_LONG).show()
                }
            })//서버에 요청
    }

    fun showLibraries(libraries: Library){//지도에 도서관 마커 표시
        val latLngBounds = LatLngBounds.Builder()//마커 영역 저장
         for(lib in libraries.SeoulPublicLibraryInfo.row){
             clusterManager.addItem(lib)
             val position = LatLng(lib.XCNTS.toDouble(), lib.YDNTS.toDouble())//마커 좌표 생성

//             val marker = MarkerOptions().position(position).title(lib.LBRRY_NAME)//좌표와 도서관 이름으로 마커 생성
//             var obj = mMap.addMarker(marker)//지도에 마커 추가
//             obj!!.tag = lib.HMPG_URL//tag 값에 홈페이지 주소 저장

             latLngBounds.include(position)//마커 추가
         }

        val bounds = latLngBounds.build()//저장해둔 마커 영역 구하기
        val padding = 0//마커 영역의 여백
        val updated = CameraUpdateFactory.newLatLngBounds(bounds, padding)//bounds, padding 으로 카메라 업데이트
        mMap.moveCamera(updated)//업데이트된 카메라를 지도에 반영
    }
}