package com.ceaver.bao.backup

import android.os.Environment
import com.ceaver.bao.addresses.Address
import com.ceaver.bao.addresses.AddressRepository
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

private const val EXPORT_DIRECTORY_NAME = "bao"
private const val ADDRESSES_FILE_NAME = "addresses.csv"

object BackupManager {

    enum class Result {
        SUCCESS,
        NO_DATA_FOUND,
        EXCEPTION
    }

    fun export(): Result {
        val addresses = AddressRepository.loadAllAddresses()
        if (addresses.isEmpty())
            return Result.NO_DATA_FOUND
        try {
            tryExport(addresses)
            return Result.SUCCESS
        } catch (e: IOException) {
            return Result.EXCEPTION
        }
    }

    @Throws(IOException::class)
    private fun tryExport(addresses: List<Address>) {
        val targetDirectory = getOrCreateDirectory()
        val filePath = targetDirectory.path + "/" + ADDRESSES_FILE_NAME
        val csvPrinter = CSVPrinter(Files.newBufferedWriter(Paths.get(filePath)), CSVFormat.DEFAULT)
        for (address in addresses) csvPrinter.printRecord(address.value, address.mapping)
        csvPrinter.flush()
    }

    @Throws(IOException::class)
    fun import(): Result {
        val sourceDirectory = getOrCreateDirectory()
        val filePath = sourceDirectory.path + "/" + ADDRESSES_FILE_NAME
        if (!File(filePath).exists())
            return Result.NO_DATA_FOUND
        try {
            tryImport(sourceDirectory)
            return Result.SUCCESS
        } catch (e: IOException) {
            return Result.EXCEPTION
        }
    }

    private fun tryImport(sourceDirectory: File) {
        val reader = Files.newBufferedReader(Paths.get(sourceDirectory.path + "/" + ADDRESSES_FILE_NAME))
        val csvParser = CSVParser(reader, CSVFormat.DEFAULT)
        val addresses = csvParser.map { Address(0, it.get(0), it.get(1)) }.toList()
        AddressRepository.deleteAllAddresses() // TODO make configurable
        AddressRepository.insertAddresses(addresses)
    }

    private fun getOrCreateDirectory(): File {
        val rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val targetDirectory = File(rootDir.path + "/" + EXPORT_DIRECTORY_NAME)
        if (!targetDirectory.exists()) targetDirectory.mkdir()
        return targetDirectory
    }
}

