package com.preetTractor.galaxyAndroid.ui.orderUi.fragment

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Application
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseSchemeList
import com.preetTractor.galaxyAndroid.searchUi.ui.activity.SearchActivity

import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.databinding.FragmentPlaceOrderBinding
import com.preetTractor.galaxyAndroid.helper.APiPayloadKeys
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import com.preetTractor.galaxyAndroid.searchUi.adapter.RecentOrderAndSearchAdapter
import com.preetTractor.galaxyAndroid.ui.orderUi.activity.ItemListFromSubCategoryOrderRequestActivity
import com.preetTractor.galaxyAndroid.ui.orderUi.activity.ItemOneActivity
import com.preetTractor.galaxyAndroid.ui.orderUi.activity.SubCategoryListActivity
import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.ItemCategoryInOrderAdapter
import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.RecentSearchAdapter
import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.SchemeSliderImageAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlaceOrderFragment : Fragment() {


      lateinit var binding: FragmentPlaceOrderBinding
      lateinit var viewModel: MainViewModel

      var isList: ArrayList<ResponseSchemeList.Data> = ArrayList()
      lateinit var adapter: SchemeSliderImageAdapter
      lateinit var categoryAdapter: ItemCategoryInOrderAdapter

      var sliderHandler = Handler()
      //lateinit var sessionManagement: SessionManagement

      private val baseText = "search for \"aloo\""
      private val newWords = listOf("Category", "Sub-Category", "Item")
      private var wordIndex = 0


      var type = "reorder"

      // Declare a global variable to hold the ObjectAnimator
      private var textAnimation: ObjectAnimator? = null

      lateinit var recentOrderAndSearchAdapter: RecentOrderAndSearchAdapter

      override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
      ): View? {
            // Inflate the layout for this fragment
            binding = FragmentPlaceOrderBinding.inflate(layoutInflater)
            return binding.root
      }

      companion object {
            //  private const val TAG = "PlaceOrderFragment"
      }

      override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            //viewModel = (activity as CustomerDetailActivity).viewModel
            setUpViewModel()
            categoryAdapter = ItemCategoryInOrderAdapter(requireContext())

            recentOrderAndSearchAdapter = RecentOrderAndSearchAdapter(requireContext())

//            binding.tvSearchDoc.text = newWords[wordIndex]
            binding.tvSearchDoc.text = "Category"
            // Initial text setup
            updateText(newWords[wordIndex])

            // startAutoUpdateText()

            // Start the automatic update coroutine
            startAutoUpdate()

            //sessionManagement = SessionManagement(requireContext())
            //viewModel.getSchemeListALlFilter(requireContext())
//            bindSchemeListObserver()
            val jsonArray = JsonArray()
            if (binding.chipReOrder.isChecked) {
                  val jsonObject = JsonObject().apply {
                        addProperty(APiPayloadKeys.CardCode, PrefsByShubh.getCardCode().toString() /*sessionManagement.getCardCode()*/)
                        addProperty(APiPayloadKeys.Type, type)
                        addProperty(APiPayloadKeys.FromDate, Globals.firstDateOfFinancialYear())
                        addProperty(APiPayloadKeys.ToDate, Globals.lastDateOfFinancialYear())
                        add(APiPayloadKeys.SearchItemCode, jsonArray)
                  }
                  //viewModel.recentSearchesAndOrders(jsonObject, requireContext())
//                  subscriebeTorecentSearchAndOrder()
            }

            binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
                  if (checkedId != ChipGroup.NO_ID) {
                        val chip = group.findViewById<Chip>(checkedId)
                        val chipText = chip?.text.toString()
                        if (chipText.equals("Recent Orders", ignoreCase = true)) {
                              type = "reorder"
                              val jsonObject = JsonObject().apply {

                                    addProperty(APiPayloadKeys.CardCode, PrefsByShubh.getCardCode().toString() /*sessionManagement.getCardCode()*/)
                                    addProperty(APiPayloadKeys.Type, type)
                                    addProperty(APiPayloadKeys.FromDate, Globals.firstDateOfFinancialYear())
                                    addProperty(APiPayloadKeys.ToDate, Globals.lastDateOfFinancialYear())
                                    add(APiPayloadKeys.SearchItemCode, jsonArray)
                              }
                              //viewModel.recentSearchesAndOrders(jsonObject, requireActivity())
//                              subscriebeTorecentSearchAndOrder()
                        } else {
                              type = "usual"
                              var recentSearchAdapter = RecentSearchAdapter(requireActivity())
                              recentSearchAdapter.submitList(
                                    AppConstants.getDataSearchItemListFromPreferences(
                                          requireActivity()
                                    )
                              )
                              binding.apply {
                                    rvBottomReOrderUsualOrder.adapter = recentSearchAdapter
                                    rvBottomReOrderUsualOrder.layoutManager =
                                          LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                                    recentSearchAdapter.notifyDataSetChanged()
                                    recentSearchAdapter.setOnItemClickListener { data, i ->
                                          when (data.Type) {
                                                "Category" -> {
                                                      val i = Intent(requireActivity(), SubCategoryListActivity::class.java)
                                                      i.putExtra("id", "" + data.Name)
                                                      startActivity(i)
                                                      //  finish()
                                                }

                                                "SubCategory" -> {
                                                      val i = Intent(
                                                            requireActivity(),
                                                            ItemListFromSubCategoryOrderRequestActivity::class.java
                                                      )
                                                      i.putExtra("id", "" + data.Name)
                                                      startActivity(i)
                                                      //  finish()
                                                }

                                                "Item" -> {
                                                      val i = Intent(requireActivity(), ItemOneActivity::class.java)
                                                      i.putExtra("id", "" + data.id)
                                                      startActivity(i)
                                                      //finish()
                                                }

                                                else -> {

                                                }
                                          }
                                    }
                              }
                        }
                  }
            }


            categoryAdapter.setOnItemClickListener { data, i ->
                  Intent(requireActivity(), ItemListFromSubCategoryOrderRequestActivity::class.java).also {
                        it.putExtra("id", data.id.toString())
                        startActivity(it)
                  }


            }

            binding.linearSearch.setOnClickListener {
                   Intent(requireActivity(), SearchActivity::class.java).also {
                         it.putExtra("where", "item")
                         it.putExtra("docId", "")
                         startActivity(it)
                   }
            }

            binding.ibSearch.setOnClickListener {
                  Intent(requireActivity(), SearchActivity::class.java).also {
                        it.putExtra("where", "item")
                        it.putExtra("docId", "")
                        startActivity(it)
                  }
            }


            viewModel.getItemAllCategoryListALlFilter(requireActivity())
            bindItemCategoryListObserver()
            //setUpSlider()
      }

      private fun setUpViewModel() {
            val dispatchers: CoroutineDispatcher = Dispatchers.Main
            val mainRepos = DefaultMainRepositories() as MainRepos
            val fanxApi: ApisInterface = ApiClient().service(requireContext())
            val viewModelProviderfactory =
                  MainViewModelProvider(Application(), mainRepos, dispatchers, fanxApi)
            viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

      }

      private fun subscriebeTorecentSearchAndOrder() {
            viewModel.recentSearchAndOrder.observe(viewLifecycleOwner, Event.EventObserver(onError = {
                  Globals.warningMessage(requireActivity(), it)
            }, onLoading = {

            }, { response ->
                  if (response.status.equals(200)) {
                        if (response.data.isNotEmpty()) {
                              recentOrderAndSearchAdapter.submitList(response.data)
                              binding.apply {
                                    rvBottomReOrderUsualOrder.adapter = recentOrderAndSearchAdapter
                                    rvBottomReOrderUsualOrder.layoutManager =
                                          LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                                    recentOrderAndSearchAdapter.notifyDataSetChanged()
                              }
                        } else {

                              recentOrderAndSearchAdapter.submitList(listOf())
                              binding.apply {
                                    rvBottomReOrderUsualOrder.adapter = recentOrderAndSearchAdapter
                                    rvBottomReOrderUsualOrder.layoutManager =
                                          LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                                    recentOrderAndSearchAdapter.notifyDataSetChanged()


                              }
                        }

                        recentOrderAndSearchAdapter.setOnItemClickListener { data, i ->
                              val i = Intent(requireActivity(), ItemOneActivity::class.java)

                              i.putExtra("id", "" + data.id)


                              startActivity(i)


                        }
                  } else if (response.status.equals(201)) {
                        Globals.warningMessage(requireActivity(), response.message)
                  } else {
                        Globals.warningMessage(requireActivity(), "Something Went Wrong")
                  }


            }))
      }

      private fun bindItemCategoryListObserver() {
            viewModel.itemCategoryListAllFilter.observe(
                  viewLifecycleOwner,
                  Event.EventObserver(onError = {
                        Globals.warningMessage(requireActivity(), it)
                  }, onLoading = {

                  }, { response ->
                        if (response.status == 200) {

                              if (response.data.isNotEmpty()) {


                                    categoryAdapter.submitList(response.data)
                                    binding.rvCategory.apply {
                                          adapter = categoryAdapter
                                          layoutManager = GridLayoutManager(activity,2, GridLayoutManager.HORIZONTAL, false)
                                          categoryAdapter.notifyDataSetChanged()
                                    }

                              }


                        } else if (response.status == 401) {
                              //sessionManagement.ClearSession()
                              PrefsByShubh.ClearSession()
                              Globals.logoutScreen(requireActivity())

                        } else {
                              Globals.warningMessage(requireContext(), response.message)
                        }
                  })
            )
      }


      private fun bindSchemeListObserver() {
            viewModel.schemeListAllFilter.observe(viewLifecycleOwner, Event.EventObserver(onError = {
                  Globals.warningMessage(requireActivity(), it)
            }, onLoading = {

            }, { response ->
                  if (response.status == 200) {

                        if (response.data.size > 0) {
                              isList.clear()
                              isList.addAll(response.data)

                              //setUpSlider()


                        }


                  } else if (response.status == 401) {
                        //sessionManagement.ClearSession()
                        PrefsByShubh.ClearSession()
                        Globals.logoutScreen(requireActivity())

                  } else {
                        Globals.warningMessage(requireContext(), response.message)
                  }
            }))
      }


      //todo code work is , whenever image 3 sec is over its automatically move to next image--
      val slideRunnable =
            Runnable { binding.viewPager.currentItem = binding.viewPager.currentItem + 1 }

      //todo its work is to run image in loop after images finish , run images from starting-
      val runnable = Runnable {
            isList.addAll(isList)

            adapter.notifyDataSetChanged()
      }


      private fun setUpSlider() {
            adapter = SchemeSliderImageAdapter(requireContext(), isList)


            adapter.notifyDataSetChanged()

            binding.viewPager.adapter = adapter

            binding.viewPager.clipChildren = false

            binding.viewPager.clipToPadding = false

            binding.viewPager.offscreenPageLimit = 3

            binding.viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

            val compositePageTransformer = CompositePageTransformer()

            compositePageTransformer.addTransformer(MarginPageTransformer(40))

            compositePageTransformer.addTransformer(object : ViewPager2.PageTransformer {
                  override fun transformPage(page: View, position: Float) {
                        val r = 1 - Math.abs(position)

                        page.scaleY = 0.85f + r * 0.15f;
                  }

            })

            binding.viewPager.setPageTransformer(compositePageTransformer)

            binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                  override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)

                        sliderHandler.removeCallbacks(slideRunnable)

                        sliderHandler.postDelayed(slideRunnable, 2000)

                        if (position == isList.size - 2) {
                              binding.viewPager.post { runnable }
                        }
                  }
            })
      }


      private fun updateText(word: String) {
            var newWord = "\"$word\""
            // val newText = baseText.replace("aloo", word)
            val newText = baseText.replace("search for \"aloo\"", word)
            val spannableString = SpannableString(newText)

            // Find the start and end indices of the word to be bolded
            val startIndex = newText.indexOf(word)
            val endIndex = startIndex + word.length
            // var subString=baseText.substring(startIndex-1,endIndex)


            // Apply bold style to the word
            spannableString.setSpan(
                  StyleSpan(Typeface.BOLD),
                  startIndex,
                  endIndex,
                  Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Apply text change with animation
            animateTextChange(spannableString)
      }

      private fun animateTextChange(newText: SpannableString) {
            // Cancel any ongoing animation
            cancelAnimation()
            // Fade out and translate up the old hint
            val fadeOut = ObjectAnimator.ofFloat(binding.tvSearchDoc, "alpha", 1f, 0f)
            val translateUp = ObjectAnimator.ofFloat(binding.tvSearchDoc, "translationY", 0f, -50f)
            fadeOut.duration = 300
            translateUp.duration = 300

            fadeOut.addListener(object : Animator.AnimatorListener {
                  override fun onAnimationStart(animation: Animator) {}
                  override fun onAnimationEnd(animation: Animator) {
                        // Set the new hint
//                        binding.tvSearchDoc.text = newText

                        // Reset the translation and fade in the new hint
                        binding.tvSearchDoc.translationY = 100f
                        val fadeIn = ObjectAnimator.ofFloat(binding.tvSearchDoc, "alpha", 0f, 1f)
                        val translateDown =
                              ObjectAnimator.ofFloat(binding.tvSearchDoc, "translationY", 100f, 0f)
                        fadeIn.duration = 1000
                        translateDown.duration = 1000

                        fadeIn.start()
                        translateDown.start()
                  }

                  override fun onAnimationCancel(animation: Animator) {}
                  override fun onAnimationRepeat(animation: Animator) {}
            })

            fadeOut.start()
            translateUp.start()
      }

      private fun cancelAnimation() {
            textAnimation?.cancel()
      }

      private fun startAutoUpdate() {
            val job = CoroutineScope(Dispatchers.Main).launch {
                  while (isActive) {
                        delay(2500) // Delay for 3 seconds
                        wordIndex = (wordIndex + 1) % newWords.size
                        updateText(newWords[wordIndex])
                  }
            }

            // Cancel the job when the activity is destroyed
            lifecycle.addObserver(LifecycleEventObserver { _, event ->
                  if (event == Lifecycle.Event.ON_DESTROY) {
                        job.cancel()
                  }
            })
      }

      override fun onPause() {
            super.onPause()

            sliderHandler.removeCallbacks(slideRunnable)
      }

      override fun onResume() {
            super.onResume()

            sliderHandler.postDelayed(slideRunnable, 2000)
      }

}