package kplyr.test

import io.kotlintest.specs.FlatSpec
import kplyr.*
import kplyr.UnequalByHelpers.joinInner

/**
require(dplyr)
iris[1, "Species"] <- NA
head(iris)
group_by(iris, Species)
group_by(iris, Species) %>% summarize(mean_length=mean(Sepal.Width))

 */
class InnerJoinTests : FlatSpec() { init {

    "it" should "perform an inner join" {
        val voreInfo = sleepData.groupBy("vore").summarize("vore_mod" to { it["vore"].asStrings().first() + "__2" })
        voreInfo.print()

        val sleepWithInfo = joinLeft(sleepData, voreInfo) // auto detect 'by' here

//        sleepWithInfo.print()
        sleepWithInfo.glimpse()

        sleepWithInfo.nrow shouldBe sleepData.nrow
        // make sure that by columns don't show up twice
        sleepWithInfo.ncol shouldBe (sleepData.ncol + 1)

        sleepWithInfo.head().print()
//        sleepWithInfo.names should contain "" // todo reenable
    }


    "it" should "allow to join by all columns" {
        joinInner(sleepData, sleepData).names shouldBe sleepData.names
    }


    "it" should "add suffices if join column names have duplicates" {
        // allow user to specify suffix
        val df = (dataFrameOf("foo", "bar"))(
                "a", 2,
                "b", 3,
                "c", 4
        )

        // join on foo
        joinInner(df, df, by = "foo", suffices = "_1" to "_2").apply {
//            names should contain element "sdf"
            print()
            (names == listOf("foo", "bar_1", "bar_2")) shouldBe true
        }

        // again but now join on bar. Join columns are expected to come first
        joinInner(df, df, by = "bar", suffices = "_1" to "_2").apply {
            (names == listOf("bar", "foo_1", "foo_2")) shouldBe true
        }

        // again but now join on nothing
        joinInner(df, df, by = emptyList(), suffices = "_1" to "_2").apply {
            nrow shouldBe 9
            names shouldEqual  listOf("foo_1", "bar_1", "foo_2", "bar_2")
        }
    }


    "it" should "allow to use different and multiple by columns"{
        joinInner(persons, weights, by = listOf("name" to "last")).apply {
            nrow shouldBe 2
        }
    }
}
}


class OuterJoinTest : FlatSpec() { init {

    "it" should "join calculate cross-product when joining on empty by list" {
        val dfA = dataFrameOf("foo", "bar")(
                "a", 2,
                "b", 3,
                "c", 4
        )
        // todo should the result be the same as for joinInner with by=emptyList() or should we prevent the empty-join for either of them??)
        joinOuter(dfA, dfA, by = emptyList()).apply {
            nrow shouldBe  9
            ncol shouldBe 4
            names shouldEqual listOf("foo.x", "bar.x", "foo.y", "bar.y")
        }
    }


    "it" should "should allow for NA in by attribute-lists" {
        //todo it's more eyefriendly if NA merge tuples come last in the result table. Can we do the same
//        TODO()
    }
}
}

class LeftJoinTest : FlatSpec() { init {


    "it" should "join calculate cross-product when joining on empty by list" {

        // todo should the result be the same as for joinInner with by=emptyList() or should we prevent the empty-join for either of them??)

//        joinOuter(persons, weights, by = "last" to "name").apply {
//            nrow shouldBe  9
//            ncol shouldBe 4
//            names shouldEqual listOf("foo.x", "bar.x", "foo.y", "bar.y")
//        }
        fail("")
    }


    "it" should "should allow for NA in by attribute-lists" {
        //todo it's more eyefriendly if NA merge tuples come last in the result table. Can we do the same
//        TODO()
    }
}
}


val persons = dataFrameOf(
        "first_name", "last_name", "age")(
        "max", "smith", 53,
        "tom", "doe", 30,
        "eva", "miller", 23
)

val weights = dataFrameOf(
        "first", "last", "weight")(
        "max", "smith", 56.3,
        "tom", "doe", null,
        "eva", "meyer", 23.3
)