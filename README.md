# nounou-times

you need postgres dabase in local ;
This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/nounou-times-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- REST resources for Hibernate ORM with Panache ([guide](https://quarkus.io/guides/rest-data-panache)): Generate Jakarta
  REST resources for your Hibernate Panache entities and repositories
- RESTEasy Classic ([guide](https://quarkus.io/guides/resteasy)): REST endpoint framework implementing Jakarta REST and
  more

## Provided Code

### REST Data with Panache

Generating Jakarta REST resources with Panache

[Related guide section...](https://quarkus.io/guides/rest-data-panache)

### RESTEasy JAX-RS

Easily start your RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started#the-jax-rs-resources)
# nounou-times

// Entity: Utilisateur
@Entity
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String motDePasse; // Password field (hashed)
    private String civilité; // e.g., "Madame", "Monsieur"
    private String typeUtilisateur; // e.g., "Parent", "Nounou"

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<Evénement> événements;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Enfant> enfants;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Nounou> nounous;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<RapportMensuel> rapports;

    // Getters and Setters
}

// Entity: Événement
@Entity
public class Evénement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String description;
    private String status; // e.g., "En attente", "Accepté", "Refusé"
    private String type; // e.g., "Heures supplémentaires", "Retard", "Demande de modification"

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    private String details; // Additional details about the event

    // Getters and Setters
}

// Entity: HeuresSupplémentaires (If needed as a specific subtype)
@Entity
public class HeuresSupplémentaires extends Evénement {

    private Duration durée;
    private boolean validation; // True if validated, false otherwise

    // Getters and Setters
}

// Entity: Enfant
@Entity
public class Enfant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private LocalDate dateNaissance;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Utilisateur parent;

    // Getters and Setters
}

// Entity: Nounou
@Entity
public class Nounou {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private LocalDate dateDébut;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Utilisateur parent;

    @OneToMany(mappedBy = "nounou", cascade = CascadeType.ALL)
    private List<Absence> absences;

    @OneToMany(mappedBy = "nounou", cascade = CascadeType.ALL)
    private List<FicheDePaie> fichesDePaie;

    // Getters and Setters
}

// Entity: RapportMensuel
@Entity
public class RapportMensuel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private YearMonth mois;
    private int heuresTotales;
    private BigDecimal montantTotal;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    // Getters and Setters
}

// Entity: Garde
@Entity
public class Garde {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private LocalTime heureDébut;
    private LocalTime heureFin;

    @ManyToOne
    @JoinColumn(name = "enfant_id")
    private Enfant enfant;

    @ManyToOne
    @JoinColumn(name = "nounou_id")
    private Nounou nounou;

    private String status; // e.g., "Déposé", "Récupéré"

    // Getters and Setters
}

// Entity: Absence
@Entity
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateDébut;
    private LocalDate dateFin;
    private String raison; // e.g., "Congé", "Maladie"

    @ManyToOne
    @JoinColumn(name = "nounou_id")
    private Nounou nounou;

    @ManyToOne
    @JoinColumn(name = "remplacant_id")
    private Nounou remplacant;

    // Getters and Setters
}

// Entity: FicheDePaie
@Entity
public class FicheDePaie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private int heuresNormales;
    private int heuresSupplémentaires;
    private BigDecimal tauxHoraire;
    private BigDecimal montantTotal;

    @ManyToOne
    @JoinColumn(name = "nounou_id")
    private Nounou nounou;

    // Getters and Setters
}
