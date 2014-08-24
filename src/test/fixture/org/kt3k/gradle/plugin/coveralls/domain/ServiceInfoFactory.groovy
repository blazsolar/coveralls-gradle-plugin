package com.github.blazsolar.gradle.coveralls.domain

class ServiceInfoFactory {

    public static createFromEnvVar(Map env) {

        if (env.get('TRAVIS') == 'true' && env.get('TRAVIS_JOB_ID') != null) {
            return new ServiceInfo('travis-ci', env.get('TRAVIS_JOB_ID'))
        }

        // cannot create service info from env var
        return null

    }

}
