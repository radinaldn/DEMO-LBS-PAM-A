package id.topapp.radinaldn.demolbspama.rest;

import id.topapp.radinaldn.demolbspama.response.ResponsePesanan;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by radinaldn on 01/11/18.
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("do_pemesanan.php")
    Call<ResponsePesanan> doPemesanan(
            @Field("makanan") String makanan,
            @Field("porsi") String porsi,
            @Field("ket") String ket,
            @Field("lat") String lat,
            @Field("lng") String lng
    );
}
