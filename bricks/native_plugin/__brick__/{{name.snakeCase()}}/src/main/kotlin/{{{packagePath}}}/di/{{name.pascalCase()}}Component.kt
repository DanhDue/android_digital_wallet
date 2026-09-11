/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.di

import {{package}}.domain.repository.{{name.pascalCase()}}Repository
import {{package}}.domain.usecase.Get{{name.pascalCase()}}DataUseCase
import {{package}}.domain.usecase.Sync{{name.pascalCase()}}DataUseCase
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [{{name.pascalCase()}}Module::class])
interface {{name.pascalCase()}}Component {
    fun get{{name.pascalCase()}}Repository(): {{name.pascalCase()}}Repository
    fun getDataUseCase(): Get{{name.pascalCase()}}DataUseCase
    fun getSyncDataUseCase(): Sync{{name.pascalCase()}}DataUseCase
    fun get{{name.pascalCase()}}ViewModel(): {{package}}.presentation.{{name.pascalCase()}}ViewModel

    @Component.Builder
    interface Builder {
        fun build(): {{name.pascalCase()}}Component
    }
}
