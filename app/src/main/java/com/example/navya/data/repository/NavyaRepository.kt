package com.example.navya.data.repository

import com.example.navya.data.local.dao.ReminderDao
import com.example.navya.data.local.entity.PlantEntity
import com.example.navya.data.local.entity.ReminderEntity
import com.example.navya.data.models.OwnedPlantDto
import com.example.navya.data.models.toEntity
import com.example.navya.data.remote.ApiService
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class NavyaRepository
@Inject
constructor(
        private val apiService: ApiService,
        private val reminderDao: ReminderDao,
        private val plantDao: com.example.navya.data.local.dao.PlantDao,
        private val ownedPlantDao: com.example.navya.data.local.dao.OwnedPlantDao
) {

        private val mockPlants =
                listOf(
                        PlantEntity(
                                id = "plant_snake_01",
                                name = "Snake Plant",
                                species = "Sansevieria trifasciata",
                                min_lux = 250,
                                max_lux = 2500,
                                water_interval_days = 14,
                                image_url =
                                        "https://djduijtarrskmezrvltw.supabase.co/storage/v1/object/public/plant-images/snake%20plant.png",
                                description =
                                        "Tolerates low light and irregular watering. Air purifer.",
                                price = 150.00,
                                currency = "INR",
                                soil = "Sandy, Well-draining",
                                difficulty = "Easy",
                                category = "Indoor"
                        ),
                        PlantEntity(
                                id = "plant_zz_01",
                                name = "ZZ Plant",
                                species = "Zamioculcas zamiifolia",
                                min_lux = 50,
                                max_lux = 500,
                                water_interval_days = 21,
                                image_url =
                                        "https://djduijtarrskmezrvltw.supabase.co/storage/v1/object/public/plant-images/zz%20plant1.png",
                                description = "Extremely drought tolerant and low light friendly.",
                                price = 350.00,
                                currency = "INR",
                                soil = "Standard potting mix",
                                difficulty = "Easy",
                                category = "Indoor"
                        ),
                        PlantEntity(
                                id = "plant_peace_01",
                                name = "Peace Lily",
                                species = "Spathiphyllum",
                                min_lux = 100,
                                max_lux = 1000,
                                water_interval_days = 5,
                                image_url =
                                        "https://djduijtarrskmezrvltw.supabase.co/storage/v1/object/public/plant-images/peace%20lily.png",
                                description =
                                        "Beautiful white flowers, prefers consistent moisture.",
                                price = 200.00,
                                currency = "INR",
                                soil = "Peat-based mix",
                                difficulty = "Medium",
                                category = "Indoor"
                        ),
                        PlantEntity(
                                id = "plant_money_01",
                                name = "Money Plant",
                                species = "Epipremnum aureum",
                                min_lux = 500,
                                max_lux = 2500,
                                water_interval_days = 7,
                                image_url =
                                        "https://djduijtarrskmezrvltw.supabase.co/storage/v1/object/public/plant-images/money%20plant.png",
                                description = "Fast growing vine, great for beginners.",
                                price = 120.00,
                                currency = "INR",
                                category = "Indoor"
                        ),
                        PlantEntity(
                                id = "plant_aloe_01",
                                name = "Aloe Vera",
                                species = "Aloe barbadensis miller",
                                min_lux = 2000,
                                max_lux = 10000,
                                water_interval_days = 14,
                                image_url =
                                        "https://djduijtarrskmezrvltw.supabase.co/storage/v1/object/public/plant-images/aloe%20vera.png",
                                description = "Medicinal gel, prefers sunny spots.",
                                price = 100.00,
                                currency = "INR",
                                category = "Outdoor"
                        ),
                        PlantEntity(
                                id = "plant_tulsi_01",
                                name = "Tulsi (Holy Basil)",
                                species = "Ocimum tenuiflorum",
                                min_lux = 2500,
                                max_lux = 10000,
                                water_interval_days = 2,
                                image_url =
                                        "https://djduijtarrskmezrvltw.supabase.co/storage/v1/object/public/plant-images/tulsi1.png",
                                description =
                                        "Known as Holy Basil, this is the most sacred plant in Indian households. It requires direct sunlight and is valued for its medicinal properties and religious significance.",
                                price = 50.00,
                                currency = "INR",
                                soil = "Well-draining, fertile",
                                difficulty = "Medium",
                                category = "Outdoor"
                        )
                )

        fun getPlants(): Flow<List<PlantEntity>> = flow {
                emit(plantDao.getPlants().firstOrNull() ?: mockPlants)

                try {
                        val remotePlants = apiService.getPlants()

                        plantDao.insertPlants(remotePlants)

                        emit(plantDao.getPlants().firstOrNull() ?: remotePlants)
                } catch (e: Exception) {

                        if (plantDao.getPlants().firstOrNull().isNullOrEmpty()) {
                                emit(mockPlants)
                        }
                }
        }

        fun getReminders(firebaseUid: String): Flow<List<ReminderEntity>> = flow {
                emit(reminderDao.getRemindersForUser(firebaseUid).firstOrNull() ?: emptyList())

                try {
                        val remoteRemindersDto = apiService.getReminders("eq.$firebaseUid")
                        val remoteRemindersEntity = remoteRemindersDto.map { it.toEntity() }
                        reminderDao.insertAll(remoteRemindersEntity)
                        emit(
                                reminderDao.getRemindersForUser(firebaseUid).firstOrNull()
                                        ?: remoteRemindersEntity
                        )
                } catch (e: Exception) {}
        }

        fun getPlantById(id: String): Flow<PlantEntity?> = flow {
                val local = plantDao.getPlantById(id)
                if (local != null) {
                        emit(local)
                } else {

                        val mock = mockPlants.find { it.id == id }
                        if (mock != null) emit(mock)
                }

                try {
                        val remotePlants = apiService.getPlants()
                        plantDao.insertPlants(remotePlants)
                        val remotePlant = remotePlants.find { it.id == id }
                        if (remotePlant != null) {
                                emit(remotePlant)
                        }
                } catch (e: Exception) {}
        }

        fun fetchOwnedPlants(userId: String): Flow<List<PlantEntity>> = flow {
                val localOwned =
                        ownedPlantDao.getOwnedPlantsForUser(userId).firstOrNull() ?: emptyList()
                val allLocalPlants = plantDao.getPlants().firstOrNull() ?: mockPlants
                val localPlants =
                        localOwned.mapNotNull { owned ->
                                allLocalPlants.find { it.id == owned.plant_id }
                        }
                if (localPlants.isNotEmpty()) emit(localPlants)

                try {
                        val ownedDtos = apiService.getOwnedPlants("eq.$userId")

                        val ownedEntities =
                                ownedDtos.map { dto ->
                                        com.example.navya.data.local.entity.OwnedPlantEntity(
                                                user_id = dto.user_id,
                                                plant_id = dto.plant_id,
                                                purchase_date = dto.purchase_date
                                        )
                                }
                        ownedPlantDao.insertAll(ownedEntities)

                        val remotePlants =
                                try {
                                        apiService.getPlants()
                                } catch (e: Exception) {
                                        emptyList()
                                }
                        if (remotePlants.isNotEmpty()) {
                                plantDao.insertPlants(remotePlants)
                        }

                        val updatedAllPlants =
                                plantDao.getPlants().firstOrNull() ?: (mockPlants + remotePlants)
                        val updatedOwned =
                                ownedDtos.mapNotNull { dto ->
                                        updatedAllPlants.find { it.id == dto.plant_id }
                                }
                        emit(updatedOwned)
                } catch (e: Exception) {}
        }

        suspend fun saveOwnedPlant(userId: String, plantId: String) {
                try {
                        ownedPlantDao.insertOwnedPlant(
                                com.example.navya.data.local.entity.OwnedPlantEntity(
                                        user_id = userId,
                                        plant_id = plantId
                                )
                        )
                } catch (e: Exception) {}

                try {
                        val dto =
                                OwnedPlantDto(
                                        user_id = userId,
                                        plant_id = plantId,
                                        purchase_date = System.currentTimeMillis()
                                )
                        apiService.addOwnedPlant(dto)
                } catch (e: Exception) {}
        }
}
