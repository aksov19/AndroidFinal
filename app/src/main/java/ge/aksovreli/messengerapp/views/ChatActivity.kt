package ge.aksovreli.messengerapp.views

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginStart
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import ge.aksovreli.messengerapp.R
import ge.aksovreli.messengerapp.databinding.ChatActivityBinding
import kotlin.math.abs

class ChatActivity: AppCompatActivity(), AppBarLayout.OnOffsetChangedListener {
    private lateinit var binding: ChatActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)
        binding = ChatActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appbar.addOnOffsetChangedListener(this)

        binding.toolbar.setNavigationOnClickListener {
            Toast.makeText(this, "navigation button clicked", Toast.LENGTH_LONG).show()
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        if (appBarLayout == null)
            return

        val maxScroll = appBarLayout.totalScrollRange
        val percentage = abs(verticalOffset) / (maxScroll.toFloat() + R.dimen.chat_info_margin)
        val percentageAbs = abs(verticalOffset) / (maxScroll.toFloat())

        binding.UserInfoLayout.scaleX = 1 - percentage
        binding.UserInfoLayout.scaleY = 1 - percentage

        var newBottomPadding = resources.getDimensionPixelOffset(R.dimen.chat_info_padding).toFloat()
        newBottomPadding *= (1 - percentageAbs)

        var newLeftPadding = resources.getDimensionPixelOffset(R.dimen.chat_info_padding).toFloat()
        newLeftPadding *= percentageAbs

        binding.userImageView.updatePadding(bottom = newBottomPadding.toInt())
        binding.UserInfoLayout.updatePadding(bottom = newBottomPadding.toInt(), left = newLeftPadding.toInt())
    }
}