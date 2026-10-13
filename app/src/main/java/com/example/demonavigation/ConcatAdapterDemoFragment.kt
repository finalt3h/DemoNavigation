package com.example.demonavigation

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

internal const val BIND_LOG_TAG = "AdapterLogs"
internal const val BIND_LOG_THRESHOLD_MS = 0.0

data class AvatarNameItem(
    val id: Long,
    val avatarResId: Int,
    val name: String
)

data class ProfileItem(
    val id: Long,
    val name: String,
    val age: Int,
    val birthday: String
)

data class PhotoItem(
    val id: Long,
    val imageResId: Int
)

data class ContactItem(
    val id: Long,
    val avatarResId: Int,
    val name: String
)

data class ConcatDemoState(
    val people: List<AvatarNameItem>,
    val profiles: List<ProfileItem>,
    val photos: List<PhotoItem>,
    val contacts: List<ContactItem>
) {
    companion object {
        fun initial(): ConcatDemoState = ConcatDemoState(
            people = List(15) { index ->
                AvatarNameItem(
                    id = index.toLong(),
                    avatarResId = avatarFor(index),
                    name = "Avatar Name ${index + 1}"
                )
            },
            profiles = List(5) { index ->
                ProfileItem(
                    id = index.toLong(),
                    name = "Tên ${index + 1}",
                    age = 20 + index,
                    birthday = "0${index + 1}/01/200${index}"
                )
            },
            photos = List(4) { index ->
                PhotoItem(
                    id = index.toLong(),
                    imageResId = imageFor(index)
                )
            },
            contacts = List(3) { index ->
                ContactItem(
                    id = index.toLong(),
                    avatarResId = avatarFor(index + 20),
                    name = "Contact ${index + 1}"
                )
            }
        )
    }
}

class ConcatDemoUpdater(
    private val random: Random = Random.Default
) {
    private var tick = 0

    fun nextState(state: ConcatDemoState): ConcatDemoState {
        tick += 1
        val updateCount = random.nextInt(from = 2, until = 11)
        val slots = (0 until totalSize(state)).shuffled(random).take(updateCount)
        var nextState = state

        slots.forEach { slot ->
            nextState = updateSlot(nextState, slot)
        }

        return nextState
    }

    private fun totalSize(state: ConcatDemoState): Int =
        state.people.size + state.profiles.size + state.photos.size + state.contacts.size

    private fun updateSlot(state: ConcatDemoState, slot: Int): ConcatDemoState {
        var cursor = slot
        if (cursor < state.people.size) {
            return state.copy(people = state.people.replace(cursor) { item ->
                when (random.nextInt(2)) {
                    0 -> item.copy(name = "Avatar Name ${item.id + 1}.$tick")
                    else -> item.copy(avatarResId = avatarFor(tick + item.id.toInt()))
                }
            })
        }

        cursor -= state.people.size
        if (cursor < state.profiles.size) {
            return state.copy(profiles = state.profiles.replace(cursor) { item ->
                when (random.nextInt(3)) {
                    0 -> item.copy(name = "Tên ${item.id + 1}.$tick")
                    1 -> item.copy(age = 18 + random.nextInt(40))
                    else -> item.copy(birthday = "%02d/%02d/%04d".format(
                        random.nextInt(1, 29),
                        random.nextInt(1, 13),
                        random.nextInt(1985, 2010)
                    ))
                }
            })
        }

        cursor -= state.profiles.size
        if (cursor < state.photos.size) {
            return state.copy(photos = state.photos.replace(cursor) { item ->
                item.copy(imageResId = imageFor(tick + item.id.toInt()))
            })
        }

        cursor -= state.photos.size
        return state.copy(contacts = state.contacts.replace(cursor) { item ->
            when (random.nextInt(2)) {
                0 -> item.copy(name = "Contact ${item.id + 1}.$tick")
                else -> item.copy(avatarResId = avatarFor(tick + item.id.toInt() + 40))
            }
        })
    }
}

class ConcatAdapterDemoViewModel : ViewModel() {
    private val updater = ConcatDemoUpdater()
    private val _state = MutableStateFlow(ConcatDemoState.initial())
    val state: StateFlow<ConcatDemoState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            while (isActive) {
                delay(1_000)
                _state.value = updater.nextState(_state.value)
            }
        }
    }
}

class ConcatAdapterDemoFragment : Fragment() {
    private val viewModel: ConcatAdapterDemoViewModel by viewModels()
    private val peopleAdapter = PeopleAdapter()
    private val profilesAdapter = ProfilesAdapter()
    private val photosAdapter = PhotosAdapter()
    private val contactsAdapter = ContactsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_concat_adapter_demo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerConcatDemo)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ConcatAdapter(
            peopleAdapter,
            profilesAdapter,
            photosAdapter,
            contactsAdapter
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    peopleAdapter.submitList(state.people)
                    profilesAdapter.submitList(state.profiles)
                    photosAdapter.submitList(state.photos)
                    contactsAdapter.submitList(state.contacts)
                }
            }
        }
    }
}

private class PeopleAdapter : ListAdapter<AvatarNameItem, PeopleAdapter.ViewHolder>(PeopleDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent.inflateItem(R.layout.item_avatar_name))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        logSlowBind(holder, position) {
            holder.bind(getItem(position))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            logSlowBind(holder, position, payloads) {
                holder.bind(getItem(position))
            }
            return
        }
        logSlowBind(holder, position, payloads) {
            holder.bindPayload(getItem(position), payloads.flattenPayloads())
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val avatar = view.findViewById<ImageView>(R.id.imgAvatar)
        private val name = view.findViewById<TextView>(R.id.txtName)

        fun bind(item: AvatarNameItem) {
            avatar.setImageResource(item.avatarResId)
            name.text = item.name
        }

        fun bindPayload(item: AvatarNameItem, payloads: Set<String>) {
            if (PeoplePayload.AVATAR in payloads) avatar.setImageResource(item.avatarResId)
            if (PeoplePayload.NAME in payloads) name.text = item.name
        }
    }
}

private class ProfilesAdapter : ListAdapter<ProfileItem, ProfilesAdapter.ViewHolder>(ProfileDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent.inflateItem(R.layout.item_profile))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        logSlowBind(holder, position) {
            holder.bind(getItem(position))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            logSlowBind(holder, position, payloads) {
                holder.bind(getItem(position))
            }
            return
        }
        logSlowBind(holder, position, payloads) {
            holder.bindPayload(getItem(position), payloads.flattenPayloads())
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name = view.findViewById<TextView>(R.id.txtName)
        private val age = view.findViewById<TextView>(R.id.txtAge)
        private val birthday = view.findViewById<TextView>(R.id.txtBirthday)

        init {
            name.enableAdapter2AutoScale()
            age.enableAdapter2AutoScale()
            birthday.enableAdapter2AutoScale()
        }

        fun bind(item: ProfileItem) {
            name.text = item.name
            age.text = "Tuổi: ${item.age}"
            birthday.text = "Ngày sinh: ${item.birthday}"
        }

        fun bindPayload(item: ProfileItem, payloads: Set<String>) {
            if (ProfilePayload.NAME in payloads) name.text = item.name
            if (ProfilePayload.AGE in payloads) age.text = "Tuổi: ${item.age}"
            if (ProfilePayload.BIRTHDAY in payloads) birthday.text = "Ngày sinh: ${item.birthday}"
        }
    }
}

private class PhotosAdapter : ListAdapter<PhotoItem, PhotosAdapter.ViewHolder>(PhotoDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent.inflateItem(R.layout.item_photo_only))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        logSlowBind(holder, position) {
            holder.bind(getItem(position))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            logSlowBind(holder, position, payloads) {
                holder.bind(getItem(position))
            }
            return
        }
        logSlowBind(holder, position, payloads) {
            holder.bind(getItem(position))
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image = view.findViewById<ImageView>(R.id.imgPhoto)

        fun bind(item: PhotoItem) {
            image.setImageResource(item.imageResId)
        }
    }
}

private class ContactsAdapter : ListAdapter<ContactItem, ContactsAdapter.ViewHolder>(ContactDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent.inflateItem(R.layout.item_avatar_name))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        logSlowBind(holder, position) {
            holder.bind(getItem(position))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            logSlowBind(holder, position, payloads) {
                holder.bind(getItem(position))
            }
            return
        }
        logSlowBind(holder, position, payloads) {
            holder.bindPayload(getItem(position), payloads.flattenPayloads())
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val avatar = view.findViewById<ImageView>(R.id.imgAvatar)
        private val name = view.findViewById<TextView>(R.id.txtName)

        fun bind(item: ContactItem) {
            avatar.setImageResource(item.avatarResId)
            name.text = item.name
        }

        fun bindPayload(item: ContactItem, payloads: Set<String>) {
            if (ContactPayload.AVATAR in payloads) avatar.setImageResource(item.avatarResId)
            if (ContactPayload.NAME in payloads) name.text = item.name
        }
    }
}

private object PeopleDiff : DiffUtil.ItemCallback<AvatarNameItem>() {
    override fun areItemsTheSame(oldItem: AvatarNameItem, newItem: AvatarNameItem): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: AvatarNameItem, newItem: AvatarNameItem): Boolean =
        oldItem == newItem

    override fun getChangePayload(oldItem: AvatarNameItem, newItem: AvatarNameItem): Any? =
        buildList {
            if (oldItem.avatarResId != newItem.avatarResId) add(PeoplePayload.AVATAR)
            if (oldItem.name != newItem.name) add(PeoplePayload.NAME)
        }.takeIf { it.isNotEmpty() }
}

private object ProfileDiff : DiffUtil.ItemCallback<ProfileItem>() {
    override fun areItemsTheSame(oldItem: ProfileItem, newItem: ProfileItem): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ProfileItem, newItem: ProfileItem): Boolean =
        oldItem == newItem

    override fun getChangePayload(oldItem: ProfileItem, newItem: ProfileItem): Any? =
        buildList {
            if (oldItem.name != newItem.name) add(ProfilePayload.NAME)
            if (oldItem.age != newItem.age) add(ProfilePayload.AGE)
            if (oldItem.birthday != newItem.birthday) add(ProfilePayload.BIRTHDAY)
        }.takeIf { it.isNotEmpty() }
}

private object PhotoDiff : DiffUtil.ItemCallback<PhotoItem>() {
    override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean =
        oldItem == newItem

    override fun getChangePayload(oldItem: PhotoItem, newItem: PhotoItem): Any? =
        PhotoPayload.IMAGE.takeIf { oldItem.imageResId != newItem.imageResId }
}

private object ContactDiff : DiffUtil.ItemCallback<ContactItem>() {
    override fun areItemsTheSame(oldItem: ContactItem, newItem: ContactItem): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ContactItem, newItem: ContactItem): Boolean =
        oldItem == newItem

    override fun getChangePayload(oldItem: ContactItem, newItem: ContactItem): Any? =
        buildList {
            if (oldItem.avatarResId != newItem.avatarResId) add(ContactPayload.AVATAR)
            if (oldItem.name != newItem.name) add(ContactPayload.NAME)
        }.takeIf { it.isNotEmpty() }
}

private object PeoplePayload {
    const val AVATAR = "avatar"
    const val NAME = "name"
}

private object ProfilePayload {
    const val NAME = "name"
    const val AGE = "age"
    const val BIRTHDAY = "birthday"
}

private object PhotoPayload {
    const val IMAGE = "image"
}

private object ContactPayload {
    const val AVATAR = "avatar"
    const val NAME = "name"
}

private fun ViewGroup.inflateItem(layoutResId: Int): View =
    LayoutInflater.from(context).inflate(layoutResId, this, false)

private fun List<Any>.flattenPayloads(): Set<String> =
    flatMap { payload ->
        when (payload) {
            is List<*> -> payload.filterIsInstance<String>()
            is String -> listOf(payload)
            else -> emptyList()
        }
    }.toSet()

private fun <T> List<T>.replace(index: Int, transform: (T) -> T): List<T> =
    mapIndexed { currentIndex, item ->
        if (currentIndex == index) transform(item) else item
    }

private fun avatarFor(seed: Int): Int =
    if (seed % 2 == 0) R.mipmap.ic_launcher else R.mipmap.ic_launcher_round

private fun imageFor(seed: Int): Int =
    if (seed % 2 == 0) R.drawable.ic_launcher_background else R.drawable.ic_launcher_foreground

private fun TextView.enableAdapter2AutoScale() {
    TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
        this,
        12,
        18,
        1,
        TypedValue.COMPLEX_UNIT_SP
    )
}

internal inline fun RecyclerView.Adapter<*>.logSlowBind(
    holder: RecyclerView.ViewHolder,
    position: Int,
    payloads: MutableList<Any>? = null,
    block: () -> Unit,
) {
    val start = System.nanoTime()
    block()
    val durationMs = (System.nanoTime() - start) / 1_000_000.0
    if (durationMs > BIND_LOG_THRESHOLD_MS) {
        Log.d(
            BIND_LOG_TAG,
            "${this.javaClass.simpleName} ${if (payloads.isNullOrEmpty()) "" else "payload"}-> " +
                "position=$position, " +
                "viewType=${holder.itemViewType}, " +
                "holder=${holder.javaClass.simpleName}, " +
                "time=${String.format(Locale.US, "%.2f", durationMs)}ms " +
                if (payloads.isNullOrEmpty()) "" else "- $payloads"
        )
    }
}
