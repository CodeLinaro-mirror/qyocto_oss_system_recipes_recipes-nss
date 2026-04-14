DESCRIPTION = "NSS ECM (Enhanced Connection Manager)"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module
inherit systemd

CLEANBROKEN = "1"

SOC="${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/:"

SRC_URI = "file://qca-nss-ecm \
	   file://files \
	   "

DEPENDS:append = " virtual/kernel qca-nss-ppe-vxlanmgr qca-nss-ppe-tunipip6"
DEPENDS:${SOC}:append = " nat46 qca-nss-sfe qca-nss-ppe qca-emesh-sp qca-ovsmgr"

RDEPENDS-${PN}:append = " iptables-mod-extra ipt-conntrack \
		ipv6 l2tp pppol2tp bonding pptp \
		pppoe nat46"

RDEPENDS-${PN}:append:ipq95xx_64 = " qca-emesh-sp"
RDEPENDS-${PN}:append:ipq95xx = " qca-emesh-sp"
RDEPENDS-${PN}:append:ipq54xx_64 = " qca-emesh-sp"
RDEPENDS-${PN}:append:ipq54xx = " qca-emesh-sp"
RDEPENDS-${PN}:append:ipq53xx_64 = " qca-emesh-sp"
RDEPENDS-${PN}:append:ipq53xx = " qca-emesh-sp"
RDEPENDS-${PN}:remove:ipq53xx_32_QRDK_256 = " bonding"
RDEPENDS-${PN}:remove:ipq53xx_32_QRDK_256 = " iptables-mod-extra"
RDEPENDS-${PN}:remove:ipq53xx_32_QRDK_256 = " ipt-conntrack"


S = "${WORKDIR}/qca-nss-ecm"

PACKAGES += "kernel-module-ecm"
INSANE_SKIP:${PN} = "dev"

ECM_MAKE_OPTS:${SOC} += "ECM_IPV6_ENABLE=y \
			ECM_FRONT_END_PPE_ENABLE=y \
			ECM_FRONT_END_SFE_ENABLE=y \
			ECM_NON_PORTED_SUPPORT_ENABLE=y \
			ECM_INTERFACE_TUNIPIP6_ENABLE=y \
			ECM_INTERFACE_GRE_TUN_ENABLE=y \
			ECM_INTERFACE_GRE_TAP_ENABLE=y \
			ECM_INTERFACE_MAP_T_ENABLE=y \
			ECM_ATH_MCAST_ENABLE=y \
			ECM_INTERFACE_BOND_ENABLE=y \
			ECM_INTERFACE_VXLAN_ENABLE=y \
			ECM_INTERFACE_IPSEC_ENABLE=y \
			ECM_XFRM_ENABLE=y \
			ECM_INTERFACE_RAWIP_ENABLE=y \
			ECM_CLASSIFIER_MSCS_SCS_ENABLE=y \
			ECM_CLASSIFIER_MSCS_ENABLE=y \
			ECM_CLASSIFIER_EMESH_ENABLE=y \
			ECM_FRONT_END_PPE_QOS_ENABLE=y \
			ECM_CLASSIFIER_WIFI_ENABLE=y \
			ECM_FRONT_END_FSE_ENABLE=y \
			ECM_INTERFACE_DSA_ENABLE=y \
			ECM_INTERFACE_L2TPV3_ENABLE=y \
			ECM_INTERFACE_PPP_ENABLE=y \
			ECM_INTERFACE_PPPOE_ENABLE=y \
			ECM_INTERFACE_BRIDGE_ISOLATION_ENABLE=y \
			"
ECM_MAKE_OPTS:ipq95xx:append = " ECM_CLASSIFIER_OVS_ENABLE=y \
				 ECM_INTERFACE_OVS_BRIDGE_ENABLE=y \
				 CONFIG_QCA_NSS_ECM_OVS=y \
				 EXAMPLES_BUILD_OVS=y \
				 EXAMPLES_BUILD_PCC=y \
				 ECM_CLASSIFIER_PCC_ENABLE=y \
				 "
ECM_MAKE_OPTS:ipq95xx_64:append = " ECM_CLASSIFIER_OVS_ENABLE=y \
				    ECM_INTERFACE_OVS_BRIDGE_ENABLE=y \
				    CONFIG_QCA_NSS_ECM_OVS=y \
				    EXAMPLES_BUILD_OVS=y \
				    EXAMPLES_BUILD_PCC=y \
				    ECM_CLASSIFIER_PCC_ENABLE=y \
				    "
ECM_MAKE_OPTS:ipq54xx:append = " ECM_CLASSIFIER_OVS_ENABLE=y \
                                 ECM_INTERFACE_OVS_BRIDGE_ENABLE=y \
                                 CONFIG_QCA_NSS_ECM_OVS=y \
                                 EXAMPLES_BUILD_OVS=y \
                                 EXAMPLES_BUILD_PCC=y \
                                 ECM_CLASSIFIER_PCC_ENABLE=y \
                                 "
ECM_MAKE_OPTS:ipq54xx_64:append = " ECM_CLASSIFIER_OVS_ENABLE=y \
                                    ECM_INTERFACE_OVS_BRIDGE_ENABLE=y \
                                    CONFIG_QCA_NSS_ECM_OVS=y \
                                    EXAMPLES_BUILD_OVS=y \
                                    EXAMPLES_BUILD_PCC=y \
                                    ECM_CLASSIFIER_PCC_ENABLE=y \
                                    "
ECM_MAKE_OPTS:ipq53xx:append = " ECM_CLASSIFIER_OVS_ENABLE=y \
				 ECM_INTERFACE_OVS_BRIDGE_ENABLE=y \
				 CONFIG_QCA_NSS_ECM_OVS=y \
				 EXAMPLES_BUILD_OVS=y \
				 EXAMPLES_BUILD_PCC=y \
				 ECM_CLASSIFIER_PCC_ENABLE=y \
				 "
ECM_MAKE_OPTS:ipq53xx_64:append = " ECM_CLASSIFIER_OVS_ENABLE=y \
				    ECM_INTERFACE_OVS_BRIDGE_ENABLE=y \
				    CONFIG_QCA_NSS_ECM_OVS=y \
				    EXAMPLES_BUILD_OVS=y \
				    EXAMPLES_BUILD_PCC=y \
				    ECM_CLASSIFIER_PCC_ENABLE=y \
				    "
ECM_MAKE_OPTS:ipq53xx_32_QRDK_256:remove = "ECM_INTERFACE_BOND_ENABLE=y \
				 ECM_INTERFACE_L2TPV3_ENABLE=y \
				"

ECM_MAKE_OPTS:ipq95xx_32_QRDK_256:remove = "ECM_INTERFACE_L2TPV3_ENABLE=y"
ECM_MAKE_OPTS:ipq96xx_32_QRDK_256:remove = "ECM_INTERFACE_L2TPV3_ENABLE=y"
ECM_MAKE_OPTS:ipq52xx_32_QRDK_256:remove = "ECM_INTERFACE_L2TPV3_ENABLE=y"
ECM_MAKE_OPTS:ipq53xx_32_QRDK_256:remove = "ECM_INTERFACE_L2TPV3_ENABLE=y"
ECM_MAKE_OPTS:ipq54xx_32_QRDK_256:remove = "ECM_INTERFACE_L2TPV3_ENABLE=y"

ECM_MAKE_OPTS:append = "${@' ECM_FRONT_END_CONN_LIMIT_ENABLE=y' if d.getVar('CONFIG_KERNEL_IPQ_MEM_PROFILE', True) == '256' else ''}"
ECM_MAKE_OPTS:append = "${@' ECM_256M_PROFILE=y' if d.getVar('CONFIG_KERNEL_IPQ_MEM_PROFILE', True) == '256' else ''}"
ECM_MAKE_OPTS:append = "${@' ECM_FRONT_END_CONN_LIMIT_ENABLE=y' if d.getVar('CONFIG_LOWMEM_FLASH', True) == 'y' else ''}"

EXTRA_CFLAGS += "-I${STAGING_INCDIR}/nat46 \
		-I${STAGING_INCDIR}/qca-nss-sfe \
		-I${STAGING_INCDIR}/qca-nss-ppe \
		-I${STAGING_INCDIR}/emesh-sp \
		-I${STAGING_INCDIR}/qca-ovsmgr \
		"

MODULE_EXTRA_SYMBOLS ="${STAGING_INCDIR}/qca-nss-sfe/Module.symvers ${STAGING_INCDIR}/qca-nss-ppe/Module.symvers \
		${STAGING_INCDIR}/qca-nss-ppe-vp/Module.symvers ${STAGING_INCDIR}/nat46/Module.symvers \
		${STAGING_INCDIR}/emesh-sp/Module.symvers \
		${STAGING_INCDIR}/qca-ovsmgr/Module.symvers ${STAGING_INCDIR}/qca-nss-ppe-vxlanmgr/Module.symvers \
		${STAGING_INCDIR}/qca-nss-ppe-tunipip6/Module.symvers \
		${STAGING_INCDIR}/qca-nss-ppe-tun/Module.symvers"

do_configure() {
	true
}

do_compile:prepend() {
	cfg="${STAGING_KERNEL_BUILDDIR}/.config"

	if [ -s "$cfg" ]; then
		if grep -q "^CONFIG_BRIDGE_MCAST_OFFLOAD=y" "$cfg"; then
			EXTRA_CFLAGS="${EXTRA_CFLAGS} -DECM_ATH_MCAST_ENABLE -I${STAGING_KERNEL_BUILDDIR}/../kernel-source/net/bridge"
		fi
	fi
}
do_compile() {
	unset LDFLAGS
	make -C "${STAGING_KERNEL_BUILDDIR}" \
		CROSS_COMPILE="${TARGET_PREFIX}" \
		ARCH="${KARCH}" \
		M="${S}" \
		EXTRA_CFLAGS="$EXTRA_CFLAGS" \
		KBUILD_EXTRA_SYMBOLS="${MODULE_EXTRA_SYMBOLS}" \
		SoC="${SOC_TYPE}" \
		${ECM_MAKE_OPTS} \
		modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ecm${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	[ -f examples/ecm_ovs${KERNEL_OBJECT_SUFFIX} ] && \
		install -m 0644 examples/ecm_ovs${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	[ -f examples/ecm_pcc_test${KERNEL_OBJECT_SUFFIX} ] && \
		install -m 0644 examples/ecm_pcc_test${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}/usr/bin
	install -m 0755 ${WORKDIR}/files/ecm_dump.sh ${D}${bindir}/ecm_dump.sh
	install -m 0755 ${WORKDIR}/files/ecm_esp_spi_accel.sh ${D}${bindir}/ecm_esp_spi_accel.sh
	install -m 0755 ${WORKDIR}/files/qca-nss-ecm ${D}${bindir}/qca-nss-ecm
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/files/qca-nss-ecm.service ${D}${systemd_unitdir}/system/qca-nss-ecm.service
	install -d ${D}${sysconfdir}/sysctl.d
	install -m 0644 ${WORKDIR}/files/qca-nss-ecm.sysctl ${D}${sysconfdir}/sysctl.d/99-qca-nss-ecm.conf
	install -d ${D}${includedir}/qca-nss-ecm
	install -m 0644 exports/* ${D}${includedir}/qca-nss-ecm/
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-ecm/Module.symvers
}

do_install:append() {
	cfg="${STAGING_KERNEL_BUILDDIR}/.config"
	ECM_CONNTRACK_MAX=16384

	if [ -f "$cfg" ]; then
		if grep -q '^CONFIG_KERNEL_IPQ_MEM_PROFILE=256' "$cfg"; then
			ECM_CONNTRACK_MAX=2048
		elif grep -q '^CONFIG_KERNEL_IPQ_MEM_PROFILE=512' "$cfg"; then
			ECM_CONNTRACK_MAX=8192
		fi
	fi

	echo "net.netfilter.nf_conntrack_max=${ECM_CONNTRACK_MAX}" \
	>> ${D}${sysconfdir}/sysctl.d/99-qca-nss-ecm.conf
}

FILES:${PN} = "${systemd_unitdir}/system/qca-nss-ecm.service \
	${bindir}/qca-nss-ecm \
	${bindir}/ecm_dump.sh \
	${bindir}/ecm_esp_spi_accel.sh \
	${sysconfdir}/sysctl.d/99-qca-nss-ecm.conf \
	"

SYSTEMD_SERVICE:${PN} += "qca-nss-ecm.service"
