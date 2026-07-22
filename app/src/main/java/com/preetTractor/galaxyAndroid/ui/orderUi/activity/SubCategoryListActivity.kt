package com.preetTractor.galaxyAndroid.ui.orderUi.activity

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.databinding.ActivitySubCategoryListBinding
import com.preetTractor.galaxyAndroid.helper.APiPayloadKeys
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import com.preetTractor.galaxyAndroid.searchUi.ui.activity.SearchActivity

import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.ItemSubCategoryInOrderAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SubCategoryListActivity : AppCompatActivity() {
      lateinit var binding: ActivitySubCategoryListBinding
      lateinit var viewModel: MainViewModel
      //lateinit var sessionManagement: SessionManagement
      var catId = ""
      lateinit var categoryAdapter: ItemSubCategoryInOrderAdapter

      private val baseText = "search for \"aloo\""
      private val newWords = listOf("Category", "Sub-Category", "Item")
      private var wordIndex = 0

      // Declare a global variable to hold the ObjectAnimator
      private var textAnimation: ObjectAnimator? = null


      private fun setUpViewModel() {
            val dispatchers: CoroutineDispatcher = Dispatchers.Main
            val mainRepos = DefaultMainRepositories() as MainRepos
            val fanxApi: ApisInterface = ApiClient().service(this)
            val viewModelProviderfactory =
                  MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
            viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

      }

      override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivitySubCategoryListBinding.inflate(layoutInflater)
            setContentView(binding.root)
            setUpViewModel()
            //supportActionBar!!.hide()
            //sessionManagement = SessionManagement(this)
            categoryAdapter = ItemSubCategoryInOrderAdapter(this)

            binding.tvSearchDoc.text = newWords[wordIndex]
            // Initial text setup
            updateText(newWords[wordIndex])

            // startAutoUpdateText()

            // Start the automatic update coroutine
            startAutoUpdate()

            catId = intent.getStringExtra("id").toString()
            viewModel.getItemAllSubCategoryListALlFilter(JsonObject().apply {
                  addProperty(APiPayloadKeys.U_UTL_ITMCT, catId)
            }, this)
            subscribeToObserver()

            binding.apply {
                  ibBack.setOnClickListener {
                        finish()
                  }
                  tvTitle.text = catId
            }

            binding.linearSearch.setOnClickListener {
                  Intent(this, SearchActivity::class.java).also {
                        it.putExtra("where","item")
                        it.putExtra("docId", "")
                        startActivity(it)
                  }
            }

            binding.ibSearch.setOnClickListener {
                  Intent(this, SearchActivity::class.java).also {
                        it.putExtra("where","item")
                        it.putExtra("docId", "")
                        startActivity(it)
                  }
            }

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
                        binding.tvSearchDoc.text = newText

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

      private fun subscribeToObserver() {
            viewModel.itemSubCategoryListAllFilter.observe(this, Event.EventObserver(onError = {
                  binding.apply {
                        spinKitLoader.visibility = View.GONE
                  }
            }, onLoading = {
                  binding.spinKitLoader.visibility = View.VISIBLE
            }, { response ->
                  if (response.status == 200) {
                        binding.spinKitLoader.visibility = View.GONE

                        if (response.data.isNotEmpty()) {


                              categoryAdapter.submitList(response.data)


                              binding.rvCategory.adapter = categoryAdapter
                              categoryAdapter.notifyDataSetChanged()
                              categoryAdapter.setOnItemClickListener { data, i ->
                                    Intent(this, ItemListFromSubCategoryOrderRequestActivity::class.java).also {
                                          it.putExtra("id", data.U_UTL_ITSBG)
                                          startActivity(it)
                                    }
                              }

                        }


                  } else if (response.status == 401) {
                        //sessionManagement.ClearSession()
                        PrefsByShubh.ClearSession()
                        Globals.logoutScreen(this)

                  } else {
                        Globals.warningMessage(this, response.message)
                  }

            }))
      }
}