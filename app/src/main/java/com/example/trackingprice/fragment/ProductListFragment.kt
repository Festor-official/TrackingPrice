package com.example.trackingprice.fragment

import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trackingprice.R
import com.example.trackingprice.adapter.ProductAdapter
import com.example.trackingprice.databinding.FragmentProductListBinding
import com.example.trackingprice.model.Product
import com.example.trackingprice.viewmodel.PriceCheckViewModel
import com.example.trackingprice.viewmodel.ProductViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.util.*

@AndroidEntryPoint
class ProductListFragment : Fragment() {


    var productPrice = ""
    var productName = ""
    private var biggestImageUrl = ""
    private val productViewModel:ProductViewModel by activityViewModels()
    private val priceCheckViewModel:PriceCheckViewModel by activityViewModels()
    private  var  parseProductJob = CoroutineScope(Dispatchers.IO)
    private val adapterCoroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var binding: FragmentProductListBinding
//    val args:ProductListFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProductListBinding.bind(view)
//        if(productViewModel.getAllProductLiveData()){
//            val linkList = arrayListOf<String>()
//            productViewModel.getAllProduct().forEach {product->
//                linkList.add(product.product_url)
//            }
//            priceCheckViewModel.checkPrice(linkList)
//        }

        val data  = requireActivity().intent.getStringExtra(Intent.EXTRA_TEXT)
//
//        val bundle = args.link

        if(data!=null && data.isNotBlank()){
            parseProductJob.launch {
                if(checkLink(data)){
                    parseProduct(data.toString())
                }
            }
        }

        var recyclerView = binding.recyclerviewProductList
        var addProductButton = binding.addProductFloatingButton

        val adapter = ProductAdapter(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        adapterCoroutineScope.launch {
            productViewModel.getAllProductLiveData().observe(requireActivity(), Observer { productList ->
                binding.noProductText.visibility = if(productList.isEmpty()) {View.VISIBLE} else{View.GONE}

                adapter.setProductList(productList)
            })
        }
        addProductButton.setOnClickListener {
            val clipboardManager = context?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val link = clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()
            if(checkLink(link) && link != null){
                binding.loadingProduct.visibility = View.VISIBLE
                parseProductJob.launch {
                    parseProduct(link)
                }
            }


        }


//        productMutableLiveData.value?.add(Product(product_id=1, product_name="Протеин сывороточный life  Life Protein, Вкусный белковый коктейль  Вафли  здоровое фитнес-питание для похудения и сушки, 907 гр, 30п. — купить в интернет-магазине OZON с быстрой доставкой", product_price=1923, product_price_history= arrayListOf<Int>(), product_image_dir="https://cdn1.ozone.ru/s3/multimedia-x/c1000/6478991265.jpg", product_price_trend="Lower"))
//        productMutableLiveData.value = productMutableLiveData.value


    }

    fun checkLink(link:String?):Boolean{
        if (link == null){
            Toast.makeText(context,"Clipboard is empty",Toast.LENGTH_SHORT).show()
            return false
        }
        if("https://www.ozon" !in link){
            Toast.makeText(context,"Link is invalid",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


    suspend fun parseProduct(link: String) {

            val doc = Jsoup.connect(link)
                .userAgent("Mozilla").get()
            val images = doc.select("img")
            productName = doc.title().replace('"', ' ').replace("'", "")

            var biggestSize = 0

            for (image in images) {

                val imageSize = Picasso.get().load(image.attr("src")).get()
                val loading = image.attr("loading")
                if (imageSize.width * imageSize.height > biggestSize && loading !="lazy") {
                    biggestSize = imageSize.width * imageSize.height
                    biggestImageUrl = image.attr("src")
                }

            }

            val price = doc.select("div")

            for (i in price) {
                if ("при оплате Ozon Картой" in i.text() && i.hasClass("_3-a5")) {
                    productPrice = i.text().removeSuffix("₽ при оплате Ozon Картой")
                    productPrice = productPrice.replace(" ", "")

                }

            }

            var product = Product(
                link,
                productName,
                productPrice.toInt(),
                ArrayList<Int>(productPrice.toInt()),
                biggestImageUrl,
                Calendar.getInstance().timeInMillis,
                0
                ,Calendar.getInstance().timeInMillis

            )
            productViewModel.insert(product)

            var productList = productViewModel.getAllProduct()

            if(productList.isNotEmpty()){
                val linkList = arrayListOf<String>()
                productList.forEach {product ->
                    linkList.add(product.product_url)
                }

                priceCheckViewModel.checkPrice(linkList)
            }

            withContext(Dispatchers.Main){
                binding.loadingProduct.visibility =View.GONE
            }

        }

    override fun onDestroy() {
        super.onDestroy()
        parseProductJob.cancel()
        adapterCoroutineScope.cancel()
    }
}