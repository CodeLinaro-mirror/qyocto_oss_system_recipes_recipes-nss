DESCRIPTION = "NSS WiFi Plugins kernel module"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

CLEANBROKEN = "1"

FILESPATH =+ "${TOPDIR}/../opensource/:"

SRC_URI = "file://qca-nss-wifi-plugins/"

DEPENDS = "virtual/kernel qca-nss-ppe qca-nss-ppe-vp qca-nss-ppe-ds"

S = "${WORKDIR}/qca-nss-wifi-plugins"

PACKAGES += "kernel-module-qca-nss-wifi-plugins"

EXTRA_CFLAGS += " \
    -I${STAGING_INCDIR}/qca-nss-ppe \
    -I${STAGING_INCDIR}/qca-nss-ppe-ds \
    -I${STAGING_INCDIR}/qca-ssdk \
    -I${STAGING_INCDIR}/qca-ssdk/fal \
    -I${STAGING_INCDIR}/qca-ssdk/init \
    -I${STAGING_INCDIR}/qca-nss-ppe-vp \
    "

MODULE_EXTRA_SYMBOLS = " \
    ${STAGING_INCDIR}/qca-nss-ppe/Module.symvers \
    ${STAGING_INCDIR}/qca-nss-ppe-vp/Module.symvers \
    ${STAGING_INCDIR}/qca-nss-ppe-ds/Module.symvers \
    ${STAGING_INCDIR}/qca-ssdk/Module.symvers \
    "

do_compile() {
    unset LDFLAGS
    make -C "${STAGING_KERNEL_BUILDDIR}" \
        CROSS_COMPILE="${TARGET_PREFIX}" \
        ARCH="${KARCH}" \
        M="${S}" \
        EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
        KBUILD_EXTRA_SYMBOLS="${MODULE_EXTRA_SYMBOLS}" \
        modules
}

do_install() {
    install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/qca-nss-wifi-plugins
    install -m 0644 qca-nss-wifi-plugins${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/qca-nss-wifi-plugins/
}

KERNEL_MODULE_AUTOLOAD += "qca-nss-wifi-plugins"

