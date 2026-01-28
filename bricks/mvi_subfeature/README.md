# MVI Sub-Feature Brick

Generates complete Clean Architecture layer files for adding a new subfeature to an existing module.

## Usage

```bash
mason make mvi_subfeature --module sample --name Detail
```

Package is auto-derived: `com.danhdue.{module}`

## Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `module` | Feature module name (snake_case) | `sample`, `my_wallet` |
| `name` | Subfeature name (PascalCase) | `Detail`, `Settings` |

## Generated Files (14 files)

```
features/{module}/src/main/kotlin/
├── data/
│   ├── di/{name}DataModule.kt
│   ├── mappers/{name}Mapper.kt
│   ├── models/{name}Dto.kt
│   └── repository/{name}RepositoryImpl.kt
├── domain/
│   ├── di/{name}DomainModule.kt
│   ├── entities/{name}Entity.kt
│   ├── repository/{name}Repository.kt
│   └── usecase/Get{name}DataUseCase.kt
└── presentation/{name}/   (camelCase)
    ├── models/{name}UiModel.kt
    ├── {name}Action.kt
    ├── {name}Event.kt
    ├── {name}State.kt
    ├── {name}ViewModel.kt
    └── {name}Screen.kt
```

## Remove a Subfeature

```bash
mason make remove_subfeature --module sample --name Detail
```
