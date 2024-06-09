interface Entity

// region Users

open class User(
    open val name: String
) : Entity {
    constructor() : this("")
}

data class Reader(
    override val name: String,
) : User(name)

data class Author(
    override val name: String,
) : User(name)

// endregion Users

// region Books

open class Book(
    open val name: String,
    open val publicationDate: String,
    open val authors: List<Author>
) : Entity {
    constructor() : this("", "", listOf())
}

data class EBook(
    override val name: String,
    override val publicationDate: String,
    override val authors: List<Author>,
    val pageCount: Int
) : Book(name, publicationDate, authors) {
    constructor(name: String, publicationDate: String, author: Author, pageCount: Int) : this(
        name = name,
        publicationDate = publicationDate,
        authors = listOf(author),
        pageCount = pageCount
    )
}

data class AudioBook(
    override val name: String,
    override val publicationDate: String,
    override val authors: List<Author>,
    val speakerName: String
) : Book(name, publicationDate, authors)

// endregion Books

// region Subscription

open class Subscription(
    open val startDate: String,
    open val endDate: String
) : Entity {
    constructor() : this("", "")
}

data class ReaderSubscription(
    val reader: Reader,
    override val startDate: String,
    override val endDate: String
) : Subscription(startDate, endDate)

// endregion Subscription

// region Concept

interface ConceptDeclaration {
    val entity: Entity
    fun isSubconcept(other: Concept): Boolean
}

data class Concept(
    override val entity: Entity,
    val parents: List<Concept> = listOf()
) : ConceptDeclaration {
    constructor(entity: Entity, parent: Concept?) : this(
        entity = entity,
        parents = parent?.let { listOf(it) } ?: listOf()
    )

    override fun isSubconcept(other: Concept): Boolean {
        return when {
            this == other -> true
            parents.any { it.isSubconcept(other) } -> true
            else -> false
        }
    }
}

infix fun Concept.IsSubconcept(other: Concept) {
    println("Is ${this} subconcept of ${other}: " +
            "${this.isSubconcept(other)}")
}

infix fun Entity.isInstance(concept: Concept) {
    println("Is ${this} instance of ${concept}: " +
            "${this.javaClass == concept.entity.javaClass}")
}

// endregion Concept

fun main() {
    // Создание пользователей
    val author1 = Author(name = "Alexander Pushkin")
    val author2 = Author(name = "Leo Tolstoy")
    val reader = Reader("Max")

    // Создание объектов книг с несколькими авторами
    val eBook = EBook(
        name = "Collected Works",
        publicationDate = "2024-01-01",
        authors = listOf(author1, author2),
        pageCount = 1500
    )
    val audioBook = AudioBook(
        name = "Great Russian Literature",
        publicationDate = "2023-05-15",
        authors = listOf(author1, author2),
        speakerName = "Speaker1"
    )

    // Создание объекта книги с одним автором
    val singleAuthorBook = EBook(
        name = "War and Peace",
        publicationDate = "1869-01-01",
        author = author2,
        pageCount = 600
    )

    // Создание подписки
    val subscription = ReaderSubscription(
        reader = reader,
        startDate = "2023-01-01",
        endDate = "2024-01-01"
    )

    // Создание концептов
    val conceptUser = Concept(User())
    val conceptReader = Concept(reader, conceptUser)
    val conceptAuthor1 = Concept(author1, conceptUser)
    val conceptAuthor2 = Concept(author2, conceptUser)

    val conceptBook = Concept(Book())
    val conceptEBook = Concept(eBook, conceptBook)
    val conceptAudioBook = Concept(audioBook, conceptBook)

    val conceptSingleAuthorBook = Concept(singleAuthorBook, conceptBook)

    val conceptSubscription = Concept(Subscription())
    val conceptReaderSubscription = Concept(subscription, conceptSubscription)

    // Проверка на сабконцепты
    conceptReader IsSubconcept conceptUser // Output: true
    conceptAuthor1 IsSubconcept conceptUser // Output: true
    conceptAuthor2 IsSubconcept conceptUser // Output: true
    println("\n")

    conceptEBook IsSubconcept conceptBook // Output: true
    conceptAudioBook IsSubconcept conceptBook // Output: true
    conceptSingleAuthorBook IsSubconcept conceptBook // Output: true
    println("\n")

    conceptAuthor1 IsSubconcept conceptReader // Output: false
    conceptEBook IsSubconcept conceptAudioBook // Output: false
    println("\n")

    conceptReaderSubscription IsSubconcept conceptReaderSubscription // Output: true
    print("------------------------------------------------------------\n")

    // Проверка отношений
    reader isInstance conceptReader // Output: true
    println("\n")

    author1 isInstance conceptAuthor1 // Output: true
    println("\n")

    subscription isInstance conceptUser // Output: false
}