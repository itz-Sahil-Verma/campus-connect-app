package com.example.campusconnectfinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.campusconnectfinal.databinding.ActivityMainBinding
import com.example.campusconnectfinal.fragments.community
import com.example.campusconnectfinal.fragments.home
import com.example.campusconnectfinal.fragments.market
import com.example.campusconnectfinal.fragments.profile
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val frame = findViewById<FrameLayout>(R.id.frameL1)
        val binding1 = findViewById<BottomNavigationView>(R.id.bottombar)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)

        // this is for upper bar
        setSupportActionBar(toolbar)
        replaceFrag(home())

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigation)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.OpenDrawer, R.string.CloseDrawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle navigation item clicks here
            // Close the navigation drawer after an item is clicked
            drawerLayout.closeDrawers()
            true
        }

        binding1.setOnItemSelectedListener {

            when(it.itemId){

                R.id.home -> {
                    replaceFrag(home())
                    setToolbarForFragment(home()) // Set the toolbar for Home fragment
                }

                R.id.market -> {
                    replaceFrag(market())
                    setToolbarForFragment(market()) // Set the toolbar for Market fragment
                }

                R.id.profile -> {
                    replaceFrag(profile())
                    setToolbarForFragment(profile()) // Set the toolbar for Profile fragment
                }

                R.id.community -> {
                    replaceFrag(community())
                    setToolbarForFragment(community()) // Set the toolbar for Profile fragment
                }
            }
            true
        }

    }


    private fun setToolbarForFragment(fragment: Fragment) {
        // Handle setting different toolbar content for different fragments
        when (fragment) {
            is home -> {
                // Customize the toolbar for the 'Home' fragment if needed
                supportActionBar?.title = "Campus Connect" // Change the toolbar title
                // You can modify other toolbar properties here
            }
            is market -> {
                // Customize the toolbar for the 'Market' fragment if needed
                supportActionBar?.title = "MarketPlace" // Change the toolbar title
                // You can modify other toolbar properties here
            }
            is profile -> {
                // Customize the toolbar for the 'Profile' fragment if needed
                supportActionBar?.title = "Profile" // Change the toolbar title
                // You can modify other toolbar properties here
            }

            is community -> {
                // Customize the toolbar for the 'Profile' fragment if needed
                supportActionBar?.title = "Campus Community" // Change the toolbar title
                // You can modify other toolbar properties here
            }
        }
    }

    private fun replaceFrag(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragtransaction = fragmentManager.beginTransaction()
        fragtransaction.replace(R.id.frameL1, fragment)
        fragtransaction.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            R.id.notifcation -> {
//                // Action for the notification icon clicked
//                // Perform your desired action here
//                // For example, navigate to a notifications screen
////                startActivity(Intent(this, NotificationsActivity::class.java))
//                return true
//            }
//            R.id.message -> {
//                // Action for the message icon clicked
//                // Perform your desired action here
//                // For example, show a dialog or navigate to a messaging screen
//
//                return true
//            }
            // Add additional cases for other menu items if needed
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_icons,menu)
        return true
    }
}