# Remove Subfeature Brick

Removes all Clean Architecture layer files for a subfeature from a module.

## Usage

```bash
mason make remove_subfeature --module sample --name Detail
```

This removes:
- `presentation/detail/` - All screen files
- `data/models/DetailDto.kt`
- `data/mappers/DetailMapper.kt`
- `domain/entities/DetailEntity.kt`
- `domain/usecase/GetDetailDataUseCase.kt`

## Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `module` | Feature module name (snake_case) | `sample`, `my_wallet` |
| `name` | Subfeature name to remove (PascalCase) | `Detail`, `Settings` |

## Warning

⚠️ This action is **irreversible**. Make sure you have committed or backed up any changes before removing a subfeature.
