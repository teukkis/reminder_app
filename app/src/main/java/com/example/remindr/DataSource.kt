package com.example.remindr

import com.example.remindr.models.Reminder

class DataSource{

    companion object{

        fun createDataSet(): ArrayList<Reminder>{
            val list = ArrayList<Reminder>()
            list.add(
                Reminder(
                    "Run to the library",
                    "12/9/2021"
                )
            )
            list.add(
                Reminder(
                    "Run to the library",
                    "3/20/2021"
                )
            )

            list.add(
                Reminder(
                    "Run to the library",
                    "03/03/2021"
                )
            )
            list.add(
                Reminder(
                    "Run to the library AGAIN",
                    "11/1/2021"
                )
            )


            return list
        }
    }
}