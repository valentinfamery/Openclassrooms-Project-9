package com.openclassrooms.realestatemanager

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import app.cash.turbine.test
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.StorageReference
import com.openclassrooms.realestatemanager.data.repository.RealEstateRepositoryImpl
import com.openclassrooms.realestatemanager.database.RealEstateRoomDatabase
import com.openclassrooms.realestatemanager.database.dao.RealEstateDao
import com.openclassrooms.realestatemanager.domain.models.PhotoWithTextFirebase
import com.openclassrooms.realestatemanager.domain.models.RealEstateDatabase
import com.openclassrooms.realestatemanager.presentation.viewModels.RealEstateViewModel
import com.openclassrooms.realestatemanager.utils.Utils
import junit.framework.Assert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class RealEstateRepositoryAndroidtest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = UnconfinedTestDispatcher()
    private val scope = TestScope(dispatcher)


    lateinit var instrumentationContext: Context

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        instrumentationContext = ApplicationProvider.getApplicationContext();
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun realEstates_returnsCorrectData() = scope.runTest{

        val firebaseFirestore = mock(FirebaseFirestore::class.java)
        val storageReference = mock(StorageReference::class.java)
        // Arrange

        val db = Room.inMemoryDatabaseBuilder(instrumentationContext, RealEstateRoomDatabase::class.java).build()


        val dao = db.realEstateDao()
        val repository = RealEstateRepositoryImpl(firebaseFirestore,storageReference,instrumentationContext,dao)

        val viewModel = RealEstateViewModel(repository)

        val realEstate1 = RealEstateDatabase(
            id = "1",
            type = "Maison",
            price = 200000,
            area = 3,
            numberRoom = "2",
            lat = 44.5,
            lng = -0.5,
            hospitalsNear = true,
            schoolsNear = false,
            shopsNear = true,
            parksNear = false,
            listPhotoWithText = listOf(
                PhotoWithTextFirebase(
                    photoSource = "photo1.jpg",
                    text = "Description de la photo 1",
                    id = "photo1"
                ),
                PhotoWithTextFirebase(
                    photoSource = "photo2.jpg",
                    text = "Description de la photo 2",
                    id = "photo2"
                )
            )
        )
        val realEstate2 = RealEstateDatabase(
            id = "2",
            type = "Appartement",
            price = 100000,
            area = 2,
            numberRoom = "1",
            lat = 45.5,
            lng = -1.5,
            hospitalsNear = false,
            schoolsNear = true,
            shopsNear = false,
            parksNear = true,
            listPhotoWithText = listOf(
                PhotoWithTextFirebase(
                    photoSource = "photo3.jpg",
                    text = "Description de la photo 3",
                    id = "photo3"
                ),
                PhotoWithTextFirebase(
                    photoSource = "photo4.jpg",
                    text = "Description de la photo 4",
                    id = "photo4"
                )
            )
        )

        val expectedRealEstates = listOf(realEstate1, realEstate2)

        dao.insertRealEstate(realEstate1)
        dao.insertRealEstate(realEstate2)

        viewModel.realEstates.test {
            val list = awaitItem()
            assert(list.contains(realEstate1))
            assert(list.contains(realEstate2))
        }








    }

    @Test
    fun refreshRealEstatesFromFirestoreTest() = scope.runTest {

        val firebaseAuth = mock(FirebaseAuth::class.java)


        val connectivityManager = mock(ConnectivityManager::class.java)
        val networkInfo = mock(NetworkInfo::class.java)
        `when`(networkInfo.isConnected).thenReturn(true)
        `when`(connectivityManager.activeNetworkInfo).thenReturn(networkInfo)


        `when`(firebaseAuth.currentUser).thenReturn(mock(FirebaseUser::class.java))



        val firebaseFirestore = mock(FirebaseFirestore::class.java)
        val mockedQuerySnapshot = mock(QuerySnapshot::class.java)
        val mockCollection = mock(CollectionReference::class.java)

        val storageReference = mock(StorageReference::class.java)
        // Arrange

        val db = Room.inMemoryDatabaseBuilder(
            instrumentationContext, RealEstateRoomDatabase::class.java).build()


        val dao = db.realEstateDao()

        val repository = RealEstateRepositoryImpl(firebaseFirestore,storageReference,instrumentationContext,dao)

        val viewModel = RealEstateViewModel(repository)


        val realEstate1 = RealEstateDatabase(
            id = "1",
            type = "Maison",
            price = 200000,
            area = 3,
            numberRoom = "2",
            lat = 44.5,
            lng = -0.5,
            hospitalsNear = true,
            schoolsNear = false,
            shopsNear = true,
            parksNear = false,
            listPhotoWithText = listOf(
                PhotoWithTextFirebase(
                    photoSource = "photo1.jpg",
                    text = "Description de la photo 1",
                    id = "photo1"
                ),
                PhotoWithTextFirebase(
                    photoSource = "photo2.jpg",
                    text = "Description de la photo 2",
                    id = "photo2"
                )
            )
        )
        val realEstate2 = RealEstateDatabase(
            id = "2",
            type = "Appartement",
            price = 100000,
            area = 2,
            numberRoom = "1",
            lat = 45.5,
            lng = -1.5,
            hospitalsNear = false,
            schoolsNear = true,
            shopsNear = false,
            parksNear = true,
            listPhotoWithText = listOf(
                PhotoWithTextFirebase(
                    photoSource = "photo3.jpg",
                    text = "Description de la photo 3",
                    id = "photo3"
                ),
                PhotoWithTextFirebase(
                    photoSource = "photo4.jpg",
                    text = "Description de la photo 4",
                    id = "photo4"
                )
            )
        )

        val expectedRealEstates = listOf(realEstate1, realEstate2)

        `when`(mockedQuerySnapshot.toObjects(RealEstateDatabase::class.java)).thenReturn(expectedRealEstates)
        val mockedTask = mock(Task::class.java) as Task<QuerySnapshot>
        `when`(mockedTask.isSuccessful).thenReturn(true)
        `when`(mockedTask.result).thenReturn(mockedQuerySnapshot)
        `when`(firebaseFirestore.collection("real_estates")).thenReturn(mockCollection)
        `when`(firebaseFirestore.collection("real_estates").get()).thenReturn(mockedTask)

         viewModel.refreshRealEstates()

        viewModel.realEstates.test {
            delay(250)
            val list = expectMostRecentItem()
            assert(list.equals(expectedRealEstates))
        }

        //for (item in result ){
            //println(item.id)
        //}

        //assert(result.equals(expectedRealEstates))


    }



}

