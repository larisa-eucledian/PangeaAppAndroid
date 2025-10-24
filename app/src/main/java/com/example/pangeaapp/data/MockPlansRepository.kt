package com.example.pangeaapp.data

import android.content.Context
import com.example.pangeaapp.core.CountryRow
import com.example.pangeaapp.core.Geography
import com.example.pangeaapp.core.PackageRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader

/**
 * TODO(MIGRATE_TO_HILT): Reemplazar AppDependencies + este constructor manual por @Inject cuando migremos a Hilt.
 */

private fun JSONObject.optStringOrNull(key: String): String? =
    if (has(key) && !isNull(key)) getString(key) else null
class MockPlansRepository(private val context: Context) : PlansRepository {

// ---------- Public API ----------

    override suspend fun getCountries(): List<CountryRow> = withContext(Dispatchers.IO) {
        val json = loadAsset("countries_mock.json")
        val arr = JSONArray(json)
        (0 until arr.length()).map { i -> countryFromJson(arr.getJSONObject(i)) }
    }

    override suspend fun getPackages(): List<PackageRow> = withContext(Dispatchers.IO) {
        val json = loadAsset("packages_mock.json")
        val root = JSONObject(json)
        parsePackagesRoot(root)
    }

    override suspend fun getPackagesByCountry(code: String): List<PackageRow> =
        withContext(Dispatchers.Default) {
            val all = getPackages()
            all.filter { pkg ->
                pkg.coverage?.any { it.equals(code, ignoreCase = true) } == true
            }
        }

    override suspend fun getPackagesForCoverage(codes: List<String>): List<PackageRow> =
        withContext(Dispatchers.Default) {
            if (codes.isEmpty()) emptyList()
            else {
                val set = codes.map { it.uppercase() }.toSet()
                getPackages().filter { pkg -> pkg.coverage?.any { it.uppercase() in set } == true }
            }
        }

// ---------- Private helpers ----------

    private fun loadAsset(fileName: String): String =
        context.assets.open(fileName).bufferedReader().use(
            BufferedReader::readText
        )

    private fun parsePackagesRoot(root: JSONObject): List<PackageRow> {
        val out = mutableListOf<PackageRow>()
        val keys = root.keys()
        while (keys.hasNext()) {
            val countryKey = keys.next()
            val arr = root.optJSONArray(countryKey) ?: continue
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                out += packageFromJson(obj)
            }
        }
        return out
    }

    private fun countryFromJson(o: JSONObject): CountryRow {
        val currenciesObj = o.optJSONObject("currencies")
        val currencies = currenciesObj?.let { cur ->
            cur.keys().asSequence().associateWith { key ->
                val c = cur.optJSONObject(key) ?: JSONObject()
                CountryRow.CurrencyInfo(
                    name = c.optString("name", key),
                    symbol = c.optStringOrNull("symbol")
                )
            }
        }

        val covered = o.optJSONArray("covered_countries")?.let { arr ->
            (0 until arr.length()).map {
                arr.getString(it)
            }
        }

        return CountryRow(
            id = o.getInt("id"),
            documentId = o.getString("documentId"),
            countryCode = o.optString("country_code", ""),
            countryName = o.optString("country_name", o.optString("country_code", "")),
            region = o.optStringOrNull("region"),
            imageUrl = o.optStringOrNull("image_url"),
            languages = null,
            currencies = currencies,
            callingCodes = null,
            geography = Geography.valueOf(o.getString("geography")),
            coveredCountries = covered,
            packageCount = if (o.has("packageCount") && !o.isNull("packageCount")) o.getInt("packageCount") else null
        )
    }

    private fun packageFromJson(o: JSONObject): PackageRow {
        return PackageRow(
            id = o.getInt("id"),
            documentId = o.getString("documentId"),
            packageId = o.getString("package_id"),
            packageName = o.getString("package"),
            validityDays = o.getInt("validity_days"),
            pricePublic = o.getDouble("price_public"),
            dataAmount = o.getString("dataAmount"),
            dataUnit = o.getString("dataUnit"),
            withSMS = o.optBoolean("withSMS", false),
            withCall = o.optBoolean("withCall", false),
            withHotspot = o.optBoolean("withHotspot", false),
            withDataRoaming = o.optBoolean("withDataRoaming", false),
            withUsageCheck = o.optBoolean("withUsageCheck", false),
            currency = o.optStringOrNull("currency"),
            geography = Geography.valueOf(o.getString("geography")),
            coverage = o.optJSONArray("coverage")?.let { arr ->
                (0 until arr.length()).map { arr.getString(it) }
            },
            countryName = o.getString("country_name")
        )

    }
    override fun getCountriesFlow(): Flow<Resource<List<CountryRow>>> {
        return flow {
            emit(Resource.Loading())
            try {
                val countries = getCountries()
                emit(Resource.Success(countries))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unknown error"))
            }
        }
    }

    override fun getPackagesFlow(): Flow<Resource<List<PackageRow>>> {
        return flow {
            emit(Resource.Loading())
            try {
                val packages = getPackages()
                emit(Resource.Success(packages))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unknown error"))
            }
        }
    }

    override fun getPackagesByCountryFlow(code: String): Flow<Resource<List<PackageRow>>> {
        return flow {
            emit(Resource.Loading())
            try {
                val packages = getPackagesByCountry(code)
                emit(Resource.Success(packages))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unknown error"))
            }
        }
    }
}