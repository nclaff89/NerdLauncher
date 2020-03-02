package com.bignerdranch.android.nerdlauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
private const val  TAG = "NerdLauncherActivity"
class NerdLauncherActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nerd_launcher)

        recyclerView = findViewById(R.id.app_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        setupAdapter()
    }

    private fun setupAdapter(){
        val startupIntent = Intent(Intent.ACTION_MAIN).apply{
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val activities = packageManager.queryIntentActivities(startupIntent, 0)
        activities.sortWith(Comparator{a,b ->
            String.CASE_INSENSITIVE_ORDER.compare(
                a.loadLabel(packageManager).toString(),
                b.loadLabel(packageManager).toString()
            )
        })

        Log.i(TAG, "Found ${activities.size} activities")
        recyclerView.adapter = ActivityAdapter(activities)
    }

    private class ActivityHolder(itemView: View):
            RecyclerView.ViewHolder(itemView), View.OnClickListener {

        /**
         * Chapter 23 challenge, Change itemView to an ImageView
         * instead of TextView
         */
        private val nameTextView = itemView as ImageView
        private lateinit var resolveInfo: ResolveInfo

        init{
            nameTextView.setOnClickListener(this)
        }

        fun bindActivity(resolveInfo: ResolveInfo){
            this.resolveInfo = resolveInfo
            val packageManager = itemView.context.packageManager
            /**
             * Instead of loadLabel, use loadIcon here
             * Then use setImageDrawable to set the icon image
             * as our list item!
             */
            val appName = resolveInfo.loadIcon(packageManager)
            nameTextView.setImageDrawable(appName)
        }

        override fun onClick(view: View){
            val activityInfo = resolveInfo.activityInfo

            val intent = Intent(Intent.ACTION_MAIN).apply{
                setClassName(activityInfo.applicationInfo.packageName,
                    activityInfo.name)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val context = view.context
            context.startActivity(intent)
        }
    }

    private class ActivityAdapter(val activities: List<ResolveInfo>):
            RecyclerView.Adapter<ActivityHolder>(){

        override fun onCreateViewHolder(container: ViewGroup, viewType: Int):
                ActivityHolder{
            val layoutInflater = LayoutInflater.from(container.context)
            val view = layoutInflater
                /**
                 * chapter 23 challenge We need to create an xml layout for a list item,
                 * that can contain an image view... I made mine JUST an image view, because
                 * i am not too concerned with looks/style at this time.
                 */
                .inflate(R.layout.recycler_list_item, container, false)
            return ActivityHolder(view)
        }

        override fun onBindViewHolder(holder: ActivityHolder, position: Int){
            val resolveInfo = activities[position]
            holder.bindActivity(resolveInfo)
        }

        override fun getItemCount(): Int{
            return activities.size
        }
    }

}
